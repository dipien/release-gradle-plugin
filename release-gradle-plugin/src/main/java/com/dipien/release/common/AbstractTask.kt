package com.dipien.release.common

import com.dipien.release.ReleaseExtension
import com.dipien.release.ReleaseGradlePlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class AbstractTask : DefaultTask() {

    @get:Input
    var verbose = false

    @TaskAction
    fun doExecute() {

        LoggerHelper.logger = logger
        LoggerHelper.verbose = verbose

        onExecute()
    }

    @Internal
    protected fun getExtension(): ReleaseExtension {
        return project.extensions.getByName(ReleaseGradlePlugin.EXTENSION_NAME) as ReleaseExtension
    }

    protected abstract fun onExecute()
}
