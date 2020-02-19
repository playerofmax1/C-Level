package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.LookupList;
import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Duration;
import java.util.Date;

public class TimeSheetDTO implements LookupList {
    private long id;
    private Date workDate;
    private Date timeIn;
    private Date timeOut;
    private Duration workHour;
    private ProjectTaskDTO projectTask;
    private ProjectDTO project;
    private TaskDTO task;
    private Duration chargeDuration;
    private String description;
    private boolean holiday;

    private int sortOrder;

    private RecordStatus status;

    public TimeSheetDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public Duration getWorkHour() {
        return workHour;
    }

    public void setWorkHour(Duration workHour) {
        this.workHour = workHour;
    }

    public ProjectTaskDTO getProjectTask() {
        return projectTask;
    }

    public void setProjectTask(ProjectTaskDTO projectTask) {
        this.projectTask = projectTask;
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

    public Duration getChargeDuration() {
        return chargeDuration;
    }

    public void setChargeDuration(Duration chargeDuration) {
        this.chargeDuration = chargeDuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TimeSheetDTO that = (TimeSheetDTO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("workDate", workDate)
                .append("timeIn", timeIn)
                .append("timeOut", timeOut)
                .append("workHour", workHour)
                .append("projectTask", projectTask)
                .append("project", project)
                .append("task", task)
                .append("chargeDuration", chargeDuration)
                .append("description", description)
                .append("holiday", holiday)
                .append("sortOrder", sortOrder)
                .append("status", status)
                .toString();
    }
}
