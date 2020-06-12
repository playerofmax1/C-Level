package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.LookupList;
import com.clevel.kudu.model.MandaysRequestType;
import com.clevel.kudu.model.RequestStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;

public class MandaysRequestDTO implements LookupList {

    private MandaysRequestType type;

    private long id;
    private ProjectTaskDTO projectTask;
    private RequestStatus status;

    private ProjectDTO project;
    private TaskDTO task;
    private UserDTO user;

    private BigDecimal extendMD;
    private Duration extendMDDuration;
    private Long extendMDMinute;
    private String description;
    private String comment;
    private boolean amdCalculation;

    private Date requestDate;
    private UserDTO modifyBy;

    private long version;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MandaysRequestType getType() {
        return type;
    }

    public void setType(MandaysRequestType type) {
        this.type = type;
    }

    public ProjectTaskDTO getProjectTask() {
        return projectTask;
    }

    public void setProjectTask(ProjectTaskDTO projectTask) {
        this.projectTask = projectTask;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public BigDecimal getExtendMD() {
        return extendMD;
    }

    public void setExtendMD(BigDecimal extendMD) {
        this.extendMD = extendMD;
    }

    public Duration getExtendMDDuration() {
        return extendMDDuration;
    }

    public void setExtendMDDuration(Duration extendMDDuration) {
        this.extendMDDuration = extendMDDuration;
    }

    public Long getExtendMDMinute() {
        return extendMDMinute;
    }

    public void setExtendMDMinute(Long extendMDMinute) {
        this.extendMDMinute = extendMDMinute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAmdCalculation() {
        return amdCalculation;
    }

    public void setAmdCalculation(boolean amdCalculation) {
        this.amdCalculation = amdCalculation;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public UserDTO getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(UserDTO modifyBy) {
        this.modifyBy = modifyBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("type", type)
                .append("id", id)
                .append("projectTask", projectTask)
                .append("status", status)
                .append("project", project)
                .append("task", task)
                .append("user", user)
                .append("extendMD", extendMD)
                .append("extendMDDuration", extendMDDuration)
                .append("extendMDMinute", extendMDMinute)
                .append("description", description)
                .append("comment", comment)
                .append("amdCalculation", amdCalculation)
                .append("requestDate", requestDate)
                .append("modifyBy", modifyBy)
                .append("version", version)
                .toString()
                .replace('=', ':');
    }
}
