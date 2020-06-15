package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.LookupList;
import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.Duration;

public class ProjectTaskDTO implements LookupList {
    private long id;
    private ProjectDTO project;
    private TaskDTO task;
    private UserDTO user;

    private BigDecimal planMD;
    private Duration planMDDuration;
    private Long planMDMinute;

    private BigDecimal extendMD;
    private Duration extendMDDuration;
    private Long extendMDMinute;

    private BigDecimal totalMD;
    private Duration totalMDDuration;
    private Long totalMDMinute;

    private BigDecimal actualMD;
    private Duration actualMDDuration;
    private Long actualMDMinute;

    private String description;

    private boolean amdCalculation;
    private BigDecimal percentAMD;

    private RecordStatus status;
    private long version;

    public ProjectTaskDTO() {
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public BigDecimal getPlanMD() {
        return planMD;
    }

    public void setPlanMD(BigDecimal planMD) {
        this.planMD = planMD;
    }

    public Duration getPlanMDDuration() {
        return planMDDuration;
    }

    public void setPlanMDDuration(Duration planMDDuration) {
        this.planMDDuration = planMDDuration;
    }

    public boolean isMoreThan99Hours() {
        if (planMDDuration == null) {
            return false;
        }
        return planMDDuration.toHours() > 99;
    }

    public Long getPlanMDMinute() {
        return planMDMinute;
    }

    public void setPlanMDMinute(Long planMDMinute) {
        this.planMDMinute = planMDMinute;
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

    public BigDecimal getTotalMD() {
        return totalMD;
    }

    public void setTotalMD(BigDecimal totalMD) {
        this.totalMD = totalMD;
    }

    public Duration getTotalMDDuration() {
        return totalMDDuration;
    }

    public void setTotalMDDuration(Duration totalMDDuration) {
        this.totalMDDuration = totalMDDuration;
    }

    public Long getTotalMDMinute() {
        return totalMDMinute;
    }

    public void setTotalMDMinute(Long totalMDMinute) {
        this.totalMDMinute = totalMDMinute;
    }

    public BigDecimal getActualMD() {
        return actualMD;
    }

    public void setActualMD(BigDecimal actualMD) {
        this.actualMD = actualMD;
    }

    public Duration getActualMDDuration() {
        return actualMDDuration;
    }

    public void setActualMDDuration(Duration actualMDDuration) {
        this.actualMDDuration = actualMDDuration;
    }

    public Long getActualMDMinute() {
        return actualMDMinute;
    }

    public void setActualMDMinute(Long actualMDMinute) {
        this.actualMDMinute = actualMDMinute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAmdCalculation() {
        return amdCalculation;
    }

    public void setAmdCalculation(boolean amdCalculation) {
        this.amdCalculation = amdCalculation;
    }

    public BigDecimal getPercentAMD() {
        return percentAMD;
    }

    public void setPercentAMD(BigDecimal percentAMD) {
        this.percentAMD = percentAMD;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("project", project)
                .append("task", task)
                .append("user", user)
                .append("planMD", planMD)
                .append("planMDDuration", planMDDuration)
                .append("planMDMinute", planMDMinute)
                .append("extendMD", extendMD)
                .append("extendMDDuration", extendMDDuration)
                .append("extendMDMinute", extendMDMinute)
                .append("totalMD", totalMD)
                .append("totalMDDuration", totalMDDuration)
                .append("totalMDMinute", totalMDMinute)
                .append("actualMD", actualMD)
                .append("actualMDDuration", actualMDDuration)
                .append("actualMDMinute", actualMDMinute)
                .append("description", description)
                .append("amdCalculation", amdCalculation)
                .append("percentAMD", percentAMD)
                .append("status", status)
                .append("version", version)
                .toString()
                .replace('=', ':');
    }
}
