package com.dipien.release.task

import com.dipien.release.common.LoggerHelper
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

open class GenerateChangelogTask : AbstractGitHubTask() {

    @get:Input
    @get:Optional
    var gitUserName: String? = null

    @get:Input
    @get:Optional
    var gitUserEmail: String? = null

    @get:Input
    @Option(description = "")
    lateinit var gitBranch: String

    override fun onExecute() {

        Thread.sleep(80 * 1000)

        if (gitUserName != null) {
            commandExecutor.execute("git config user.name $gitUserName")
        }
        if (gitUserEmail != null) {
            commandExecutor.execute("git config user.email $gitUserEmail")
        }

        commandExecutor.execute("github_changelog_generator --no-unreleased --no-pull-requests --no-pr-wo-labels --exclude-labels task -t $gitHubWriteToken")
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
