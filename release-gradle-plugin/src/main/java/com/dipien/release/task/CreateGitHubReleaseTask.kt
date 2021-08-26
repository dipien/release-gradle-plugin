package com.dipien.release.task

import com.dipien.release.common.LoggerHelper
import com.dipien.github.IRepositoryIdProvider
import com.dipien.github.Release
import com.dipien.github.client.GitHubClient
import com.dipien.github.service.ReleaseService
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option
import java.io.File

open class CreateGitHubReleaseTask : AbstractGitHubTask() {

    init {
        description = "Create a GitHub Release"
    }

    @get:Input
    @get:Optional
    var gitHubUserName: String? = null

    @get:Input
    @get:Optional
    var gitHubUserEmail: String? = null

    @get:Input
    @Option(description = "")
    lateinit var gitBranch: String

    @get:Input
    @get:Optional
    @Option(description = "")
    var releaseNotes: String? = null

    override fun onExecute() {
        val client: GitHubClient = createGitHubClient()
        val tagName = "v" + project.version
        val repositoryIdProvider = getRepositoryId()
        val releaseService = ReleaseService(client)
        val release: Release? = releaseService.getReleaseByTagName(repositoryIdProvider, tagName)
        if (release == null) {
            if (releaseNotes == null) {
                releaseNotes = fetchReleaseNotes()
            }
            createRelease(releaseService, repositoryIdProvider, tagName, releaseNotes!!)
            LoggerHelper.log("GitHub release created: $tagName")
        } else {
            logger.warn("Skipping $tagName release creation because it already exists.")
        }
    }

    private fun fetchReleaseNotes(): String {
        commandExecutor.execute("github_changelog_generator --unreleased-only --no-compare-link --no-pull-requests --no-pr-wo-labels --exclude-labels task -t $gitHubWriteToken")
        val changeLogFile = File(project.rootDir, "/CHANGELOG.md")
        var started = false
        var exit = false
        val builder = StringBuilder()
        changeLogFile.readLines().forEach { line ->
            if (!exit) {
                if (!started && line.startsWith("## [Unreleased")) {
                    started = true
                } else if (started) {
                    if (line.contains("This Change Log was automatically generated")) {
                        exit = true
                    } else {
                        builder.append(line)
                        builder.append('\n')
                    }
                }
            }
        }

        if (gitHubUserName != null) {
            commandExecutor.execute("git config user.name $gitHubUserName")
        }
        if (gitHubUserEmail != null) {
            commandExecutor.execute("git config user.email $gitHubUserEmail")
        }
        commandExecutor.execute("git add -A")
        commandExecutor.execute("git stash")
        return builder.toString().trim()
    }

    private fun createRelease(releaseService: ReleaseService, repositoryIdProvider: IRepositoryIdProvider, name: String, body: String) {
        val release = Release()
        release.body = body
        release.isDraft = false
        release.name = name
        release.tagName = name
        release.isPrerelease = false
        release.targetCommitish = gitBranch
        releaseService.createRelease(repositoryIdProvider, release)
    }
}
