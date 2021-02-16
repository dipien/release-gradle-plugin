package com.dipien.release.common

import com.dipien.release.ReleaseExtension
import com.dipien.release.ReleaseGradlePlugin
import com.dipien.release.common.cli.CommandExecutor
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class AbstractTask : DefaultTask() {

    @get:Internal
    protected val commandExecutor: CommandExecutor by lazy {
        CommandExecutor(project, LogLevel.DEBUG)
    }

    @get:Input
    var verbose = false

    @get:Input
    lateinit var defaultBranch: String

    @get:Input
    lateinit var releaseBranch: String

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
