package com.classdojo.gradle.android.git

import com.dipien.release.common.LoggerHelper
import com.dipien.release.task.AbstractGitHubTask
import com.dipien.github.service.IssueService
import com.dipien.github.service.PullRequestService
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option
import java.lang.RuntimeException

open class AutomergePullRequestTask : AbstractGitHubTask() {

    @get:Input
    @Option(description = "")
    lateinit var pullRequestNumber: String

    init {
        description = "Automerge a Pull Request if no conflicts and the [automerge] label is assigned to it"
    }

    companion object {
        const val TASK_NAME = "automergePullRequest"
        const val AUTOMERGE_LABEL = "automerge"
    }

    override fun onExecute() {

        val pullRequestNumber = pullRequestNumber.toInt()

        val client = createGitHubClient()

        val repositoryIdProvider = getRepositoryId()

        val issueService = IssueService(client)
        val issue = issueService.getIssue(repositoryIdProvider, pullRequestNumber)
        if (issue.labels.find { it.name == AUTOMERGE_LABEL } != null) {
            val pullRequestService = PullRequestService(client)
            try {
                val mergeStatus = pullRequestService.merge(repositoryIdProvider, pullRequestNumber, "Automatic merge")
                if (!mergeStatus.isMerged) {
                    throw RuntimeException()
                }
            } catch (e: Exception) {
                val pullRequestComment = "The automatic pull request merging failed. Please manually merge it."
                issueService.createComment(repositoryIdProvider, pullRequestNumber, pullRequestComment)
                LoggerHelper.log(pullRequestComment)
            }
        } else {
            LoggerHelper.log("[$AUTOMERGE_LABEL] label not found for pull request #$pullRequestNumber. Skipping automerge")
        }
    }
}
