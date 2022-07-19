package com.dipien.release

import com.dipien.release.common.propertyResolver
import org.gradle.api.Project

open class ReleaseExtension(project: Project) {

    var gitHubWriteToken = project.propertyResolver.getStringProp("GITHUB_WRITE_TOKEN")
    var gitHubRepositoryOwner = project.propertyResolver.getStringProp("GITHUB_REPOSITORY_OWNER")
    var gitHubRepositoryName = project.propertyResolver.getStringProp("GITHUB_REPOSITORY_NAME")

    var gitUserName = project.propertyResolver.getStringProp("GIT_USER_NAME")
    var gitUserEmail = project.propertyResolver.getStringProp("GIT_USER_EMAIL")

    var defaultBranch = "master"
    var releaseBranch = "master"
}
