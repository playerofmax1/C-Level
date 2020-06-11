package com.clevel.kudu.front.attributes;

import com.clevel.kudu.dto.working.ProjectTaskDTO;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MandaysRequestOpenAttributes {

    private ProjectTaskDTO projectTask;

    public ProjectTaskDTO getProjectTask() {
        return projectTask;
    }

    public void setProjectTask(ProjectTaskDTO projectTask) {
        this.projectTask = projectTask;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("projectTask", projectTask)
                .toString()
                .replace('=', ':');
    }
}
