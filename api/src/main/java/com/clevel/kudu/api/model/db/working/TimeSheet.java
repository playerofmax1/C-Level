package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.converter.DurationConverter;
import com.clevel.kudu.api.model.converter.RecordStatusConverter;
import com.clevel.kudu.api.model.db.AbstractAuditEntity;
import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.Duration;
import java.util.Date;

@Entity
@Table(name = "wrk_timesheet")
public class TimeSheet extends AbstractAuditEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "workDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date workDate;
    @Column(name = "timeIn")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeIn;
    @Column(name = "timeOut")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeOut;
    @Column(name = "workHour")
    @Convert(converter = DurationConverter.class)
    private Duration workHour;
    @Column(name = "workHourMinute")
    private long workHourMinute;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectTaskId")
    private ProjectTask projectTask;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId")
    private Task task;
    @Column(name = "chargeDuration")
    @Convert(converter = DurationConverter.class)
    private Duration chargeDuration;
    @Column(name = "chargeMinute")
    private long chargeMinute;
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "sortOrder")
    private int sortOrder;
    @Column(name = "holiday")
    private boolean holiday;

    @Column(name = "status")
    @Convert(converter = RecordStatusConverter.class)
    private RecordStatus status;

    public TimeSheet() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public long getWorkHourMinute() {
        return workHourMinute;
    }

    public void setWorkHourMinute(long workHourMinute) {
        this.workHourMinute = workHourMinute;
    }

    public ProjectTask getProjectTask() {
        return projectTask;
    }

    public void setProjectTask(ProjectTask projectTask) {
        this.projectTask = projectTask;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Duration getChargeDuration() {
        return chargeDuration;
    }

    public void setChargeDuration(Duration chargeDuration) {
        this.chargeDuration = chargeDuration;
    }

    public long getChargeMinute() {
        return chargeMinute;
    }

    public void setChargeMinute(long chargeMinute) {
        this.chargeMinute = chargeMinute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("user", user)
                .append("workDate", workDate)
                .append("timeIn", timeIn)
                .append("timeOut", timeOut)
                .append("workHour", workHour)
                .append("workHourMinute", workHourMinute)
                .append("projectTask", projectTask)
                .append("project", project)
                .append("task", task)
                .append("chargeDuration", chargeDuration)
                .append("chargeMinute", chargeMinute)
                .append("description", description)
                .append("sortOrder", sortOrder)
                .append("holiday", holiday)
                .append("status", status)
                .toString();
    }
}
