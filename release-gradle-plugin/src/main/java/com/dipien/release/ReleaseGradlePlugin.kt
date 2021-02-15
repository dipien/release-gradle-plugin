package com.dipien.release

import com.dipien.release.task.TemplateTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReleaseGradlePlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "release"
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, ReleaseExtension::class.java)

        val task = project.tasks.create(TemplateTask.TASK_NAME, TemplateTask::class.java)
    }
}
