package com.dipien.release.task

import com.dipien.release.common.AbstractTask

open class TemplateTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "template"
    }

    init {
        group = "Template Task Group"
        description = "Template Task Description"
    }

    override fun onExecute() {
    }
}
