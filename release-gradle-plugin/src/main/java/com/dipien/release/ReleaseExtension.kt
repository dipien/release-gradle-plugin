package com.dipien.release

import com.dipien.release.common.propertyResolver
import org.gradle.api.Project

open class ReleaseExtension(project: Project) {

    var gitHubWriteToken = project.propertyResolver.getRequiredStringProp("GITHUB_WRITE_TOKEN")
    var gitHubRepositoryOwner = project.propertyResolver.getRequiredStringProp("GITHUB_REPOSITORY_OWNER")
    var gitHubRepositoryName = project.propertyResolver.getRequiredStringProp("GITHUB_REPOSITORY_NAME")
    var gitHubUserName = project.propertyResolver.getStringProp("GITHUB_USER_NAME")
    var gitHubUserEmail = project.propertyResolver.getStringProp("GITHUB_USER_EMAIL")

    var defaultBranch = "master"
    var releaseBranch = "production"
}
