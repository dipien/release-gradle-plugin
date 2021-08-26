package com.dipien.release

import com.dipien.release.common.propertyResolver
import org.gradle.api.Project

open class ReleaseExtension(project: Project) {

    var gitHubWriteToken = project.propertyResolver.getStringProp("GITHUB_WRITE_TOKEN")
    var gitHubRepositoryOwner = project.propertyResolver.getStringProp("GITHUB_REPOSITORY_OWNER")
    var gitHubRepositoryName = project.propertyResolver.getStringProp("GITHUB_REPOSITORY_NAME")

    // TODO Rename this to gitUserName
    var gitHubUserName = project.propertyResolver.getStringProp("GITHUB_USER_NAME")
    // TODO Rename this to gitUserEmail
    var gitHubUserEmail = project.propertyResolver.getStringProp("GITHUB_USER_EMAIL")

    var defaultBranch = "master"
    var releaseBranch = "production"
}
