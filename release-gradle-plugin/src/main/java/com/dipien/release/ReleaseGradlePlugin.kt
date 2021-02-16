package com.dipien.release

import com.classdojo.gradle.android.git.AutomergePullRequestTask
import com.classdojo.gradle.android.git.SendMergePullRequestTask
import com.dipien.release.common.AbstractTask
import com.dipien.release.task.AbstractGitHubTask
import com.dipien.release.task.CloseGitHubMilestoneTask
import com.dipien.release.task.CreateGitHubReleaseTask
import com.dipien.release.task.GenerateChangelogTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReleaseGradlePlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "release"
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, ReleaseExtension::class.java, project)

        val createGitHubReleaseTask: CreateGitHubReleaseTask = project.tasks.create("createGitHubRelease", CreateGitHubReleaseTask::class.java)
        val closeGitHubMilestoneTask: CloseGitHubMilestoneTask = project.tasks.create("closeGitHubMilestone", CloseGitHubMilestoneTask::class.java)
        val generateChangelogTask: GenerateChangelogTask = project.tasks.create("generateChangelog", GenerateChangelogTask::class.java)
        val automergePullRequestTask: AutomergePullRequestTask = project.tasks.create(AutomergePullRequestTask.TASK_NAME, AutomergePullRequestTask::class.java)
        val sendMergePullRequestTask: SendMergePullRequestTask = project.tasks.create(SendMergePullRequestTask.TASK_NAME, SendMergePullRequestTask::class.java)

        project.afterEvaluate {
            configureAbstractGitHubTask(createGitHubReleaseTask, extension)
            configureAbstractGitHubTask(closeGitHubMilestoneTask, extension)
            configureAbstractGitHubTask(automergePullRequestTask, extension)
            configureAbstractGitHubTask(generateChangelogTask, extension)
            generateChangelogTask.gitHubUserName = extension.gitHubUserName
            generateChangelogTask.gitHubUserEmail = extension.gitHubUserEmail

            configureAbstractTask(sendMergePullRequestTask, extension)
        }
    }

    private fun configureAbstractTask(task: AbstractTask, extension: ReleaseExtension) {
        task.defaultBranch = extension.defaultBranch
        task.releaseBranch = extension.releaseBranch
    }

    private fun configureAbstractGitHubTask(task: AbstractGitHubTask, extension: ReleaseExtension) {
        configureAbstractTask(task, extension)
        task.gitHubRepositoryOwner = extension.gitHubRepositoryOwner
        task.gitHubRepositoryName = extension.gitHubRepositoryName
        task.gitHubWriteToken = extension.gitHubWriteToken
    }
}
