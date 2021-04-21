package com.dipien.release.task

import com.dipien.release.common.LoggerHelper
import com.jdroid.github.Milestone
import com.jdroid.github.service.MilestoneService
import java.io.IOException
import java.util.Date

open class CloseGitHubMilestoneTask : AbstractGitHubTask() {

    init {
        description = "Close the GitHub Milestone"
    }

    @Throws(IOException::class)
    override fun onExecute() {
        val milestoneTitle = "v" + project.version
        val milestoneService = MilestoneService(createGitHubClient())
        val repositoryIdProvider = getRepositoryId()
        val milestone = milestoneService.getMilestones(repositoryIdProvider, "open").find { it.title == milestoneTitle }
        if (milestone != null) {
            val newMilestone = Milestone()
            newMilestone.number = milestone.number
            newMilestone.title = milestone.title
            newMilestone.description = milestone.description
            newMilestone.dueOn = Date()
            newMilestone.state = "closed"
            milestoneService.editMilestone(repositoryIdProvider, newMilestone)
            LoggerHelper.log("Milestone #${milestone.number} closed. [${getRepositoryUrl()}/milestones/${milestone.number}]")
        }
    }
}
