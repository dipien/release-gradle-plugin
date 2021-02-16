package com.dipien.release.task

import com.dipien.release.common.AbstractTask
import com.jdroid.github.client.GitHubClient
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

abstract class AbstractGitHubTask : AbstractTask() {

    @get:Input
    lateinit var gitHubRepositoryOwner: String

    @get:Input
    lateinit var gitHubRepositoryName: String

    @get:Input
    lateinit var gitHubWriteToken: String

    protected fun createGitHubClient(gitHubWriteToken: String): GitHubClient {
        val client = GitHubClient()
        client.setSerializeNulls(false)
        client.setOAuth2Token(gitHubWriteToken)
        return client
    }

    @Internal
    fun getRepositoryUrl(): String {
        return "git@github.com:$gitHubRepositoryOwner/$gitHubRepositoryName.git"
    }
}
