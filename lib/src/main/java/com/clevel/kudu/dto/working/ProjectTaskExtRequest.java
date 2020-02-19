package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ProjectTaskExtRequest {
    private long userId;
    private ProjectTaskDTO projectTaskDTO;
    private ProjectTaskExtDTO projectTaskExtDTO;

    public ProjectTaskExtRequest() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public ProjectTaskDTO getProjectTaskDTO() {
        return projectTaskDTO;
    }

    public void setProjectTaskDTO(ProjectTaskDTO projectTaskDTO) {
        this.projectTaskDTO = projectTaskDTO;
    }

    public ProjectTaskExtDTO getProjectTaskExtDTO() {
        return projectTaskExtDTO;
    }

    public void setProjectTaskExtDTO(ProjectTaskExtDTO projectTaskExtDTO) {
        this.projectTaskExtDTO = projectTaskExtDTO;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("userId", userId)
                .append("projectTaskDTO", projectTaskDTO)
                .append("projectTaskExtDTO", projectTaskExtDTO)
                .toString();
    }
}
