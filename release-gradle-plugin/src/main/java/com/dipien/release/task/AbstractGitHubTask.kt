package com.dipien.release.task

import com.dipien.release.common.AbstractTask
import com.dipien.github.RepositoryId
import com.dipien.github.client.GitHubClient
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import java.lang.IllegalArgumentException

abstract class AbstractGitHubTask : AbstractTask() {

    @get:Input
    @get:Optional
    var gitHubRepositoryOwner: String? = null

    @get:Input
    @get:Optional
    var gitHubRepositoryName: String? = null

    @get:Input
    @get:Optional
    var gitHubWriteToken: String? = null

    protected fun createGitHubClient(): GitHubClient {
        if (gitHubWriteToken == null) {
            throw IllegalArgumentException("gitHubWriteToken is required")
        }
        val client = GitHubClient()
        client.setSerializeNulls(false)
        client.setOAuth2Token(gitHubWriteToken)
        return client
    }

    @Internal
    fun getRepositoryUrl(): String {
        return "https://github.com/${gitHubRepositoryOwner!!}/${gitHubRepositoryName!!}"
    }
    @Internal
    fun getRepositoryId(): RepositoryId {
        return RepositoryId.create(gitHubRepositoryOwner!!, gitHubRepositoryName!!)
    }
}
