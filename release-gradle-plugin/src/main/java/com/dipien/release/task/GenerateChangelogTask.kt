package com.dipien.release.task

import com.dipien.release.common.LoggerHelper
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

open class GenerateChangelogTask : AbstractGitHubTask() {

    @get:Input
    @get:Optional
    var gitHubUserName: String? = null

    @get:Input
    @get:Optional
    var gitHubUserEmail: String? = null

    @get:Input
    @Option(description = "")
    var gitBranch = releaseBranch

    override fun onExecute() {

        Thread.sleep(80 * 1000)

        if (gitHubUserName != null) {
            commandExecutor.execute("git config user.name $gitHubUserName")
        }
        if (gitHubUserEmail != null) {
            commandExecutor.execute("git config user.email $gitHubUserEmail")
        }

        commandExecutor.execute("github_changelog_generator --no-unreleased --no-pull-requests --no-pr-wo-labels --exclude-labels task -u $gitHubRepositoryOwner -p $gitHubRepositoryName -t $gitHubWriteToken")
        commandExecutor.execute("git add CHANGELOG.md")
        val result: org.gradle.process.ExecResult =
            commandExecutor.execute("git commit -m \"Updated CHANGELOG.md\"", project.rootProject.projectDir, logStandardOutput = true, logErrorOutput = true)
        if (result.exitValue == 0) {
            commandExecutor.execute("git diff HEAD")
            commandExecutor.execute("git push origin HEAD:$gitBranch")
            LoggerHelper.log("CHANGELOG.md updated at " + getRepositoryUrl() + "/blob/$gitBranch/CHANGELOG.md")
        } else {
            LoggerHelper.warn("Skipping CHANGELOG update because it already exists.")
        }
    }
}
