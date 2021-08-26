package com.dipien.release.task

import com.classdojo.gradle.android.git.AutomergePullRequestTask
import com.dipien.release.common.AbstractTask
import com.dipien.release.common.LoggerHelper
import com.dipien.github.PullRequest
import com.dipien.github.RepositoryId
import com.dipien.github.client.GitHubClient
import com.dipien.github.client.RequestException
import com.dipien.github.service.IssueService
import com.dipien.github.service.LabelsService
import com.dipien.github.service.PullRequestService
import com.dipien.github.service.ReviewRequestsService
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option
import java.io.IOException

open class SendMergePullRequestTask : AbstractTask() {

    @get:Input
    lateinit var gitHubRepositoryOwner: String

    @get:Input
    lateinit var gitHubRepositoryName: String

    @get:Input
    lateinit var gitHubWriteToken: String

    @get:Input
    @Option(description = "")
    lateinit var baseBranch: String

    @get:Input
    @Option(description = "")
    lateinit var headBranch: String

    @get:Input
    @get:Optional
    @Option(description = "")
    var pullRequestTitle: String? = null

    @get:Input
    @get:Optional
    @Option(description = "")
    var pullRequestBody: String? = null

    @get:Input
    @get:Optional
    @Option(description = "")
    var pullRequestReviewers: List<String>? = null

    @get:Input
    @get:Optional
    @Option(description = "")
    var pullRequestTeamReviewers: List<String>? = null

    @get:Input
    @Option(description = "")
    var addAutomergeLabel: Boolean = true

    init {
        description = "Create a PR from headBranch to baseBranch when there are changes"
    }

    companion object {
        const val TASK_NAME = "sendMergePullRequest"
    }

    override fun onExecute() {

        commandExecutor.execute("git checkout $headBranch")

        val client = GitHubClient()
        client.setSerializeNulls(false)
        client.setOAuth2Token(gitHubWriteToken)

        val repositoryIdProvider = RepositoryId.create(gitHubRepositoryOwner, gitHubRepositoryName)
        val pullRequestService = PullRequestService(client)

        try {
            var pullRequest: PullRequest? = pullRequestService.getPullRequest(
                repositoryIdProvider,
                IssueService.STATE_OPEN,
                "$gitHubRepositoryOwner:$headBranch",
                baseBranch
            )
            if (pullRequest == null) {
                if (pullRequestBody == null) {
                    val bodyBuilder = StringBuilder()
                    bodyBuilder.append("This PR was automatically generated. If the **automerge** label is assigned, the PR will be automatically merged when these conditions are satisfied:\n")
                    bodyBuilder.append("- The Circle CI workflow finishes with a successful\n")
                    bodyBuilder.append("- There aren't conflicts\n\n")
                    bodyBuilder.append("Please follow these instructions before manually merging this PR:\n")
                    bodyBuilder.append("- If you have a conflict on the **version**, please resolve it assigning the higher version.\n")
                    bodyBuilder.append("- Use the **Create a merge commit** merge option")
                    pullRequestBody = bodyBuilder.toString()
                }

                if (pullRequestTitle == null) {
                    pullRequestTitle = "Merge from branch $headBranch to $baseBranch"
                }

                pullRequest = pullRequestService.createPullRequest(
                    repositoryIdProvider,
                    pullRequestTitle,
                    pullRequestBody,
                    "$gitHubRepositoryOwner:$headBranch",
                    baseBranch
                )
                LoggerHelper.log("Pull Request #" + pullRequest!!.number + " from " + headBranch + " to $baseBranch created")

                if (!pullRequestReviewers.isNullOrEmpty() || !pullRequestTeamReviewers.isNullOrEmpty()) {
                    val reviewRequestsService = ReviewRequestsService(client)
                    reviewRequestsService.createReviewRequest(repositoryIdProvider, pullRequest.number, pullRequestReviewers, pullRequestTeamReviewers)
                    LoggerHelper.log("Reviewers assigned to pull request #" + pullRequest.number)
                }

                if (addAutomergeLabel) {
                    val labelsService = LabelsService(client)
                    labelsService.addLabelsToIssue(repositoryIdProvider, pullRequest.number, listOf(AutomergePullRequestTask.AUTOMERGE_LABEL))
                    LoggerHelper.log("${AutomergePullRequestTask.AUTOMERGE_LABEL} label assigned to pull request #" + pullRequest.number)
                }
            } else {
                LoggerHelper.log("Pull Request from $headBranch to $baseBranch not created. Pull request already exists.")
            }
        } catch (e: RequestException) {
            if (e.message?.contains(" No commits between") == true) {
                LoggerHelper.log("Pull Request from $headBranch to $baseBranch not created. Nothing to merge, so the $headBranch will be removed")
                commandExecutor.execute("git push origin --delete $headBranch")
            } else {
                throw RuntimeException(e)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
