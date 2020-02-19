package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.converter.DurationConverter;
import com.clevel.kudu.api.model.converter.RecordStatusConverter;
import com.clevel.kudu.api.model.db.AbstractAuditEntity;
import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Entity
@Table(name = "wrk_prj_task")
public class ProjectTask extends AbstractAuditEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "planMD")
    private BigDecimal planMD;
    @Column(name = "planMDDuration")
    @Convert(converter = DurationConverter.class)
    private Duration planMDDuration;
    @Column(name = "planMDMinute")
    private Long planMDMinute;

    @Column(name = "extendMD")
    private BigDecimal extendMD;
    @Column(name = "extendMDDuration")
    @Convert(converter = DurationConverter.class)
    private Duration extendMDDuration;
    @Column(name = "extendMDMinute")
    private Long extendMDMinute;

    @Column(name = "actualMD")
    private BigDecimal actualMD;
    @Column(name = "actualMDDuration")
    @Convert(converter = DurationConverter.class)
    private Duration actualMDDuration;
    @Column(name = "actualMDMinute")
    private Long actualMDMinute;
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectTaskExt> extendTasks;

    @Column(name = "amdCalculation")
    private boolean amdCalculation;
    @Column(name = "percentAMD")
    private BigDecimal percentAMD;

    @Column(name = "status")
    @Convert(converter = RecordStatusConverter.class)
    private RecordStatus status;

    public ProjectTask() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    public List<ProjectTaskExt> getExtendTasks() {
        return extendTasks;
    }

    public void setExtendTasks(List<ProjectTaskExt> extendTasks) {
        this.extendTasks = extendTasks;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
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
                .append("actualMD", actualMD)
                .append("actualMDDuration", actualMDDuration)
                .append("actualMDMinute", actualMDMinute)
                .append("description", description)
                .append("extendTasks", extendTasks)
                .append("amdCalculation", amdCalculation)
                .append("percentAMD", percentAMD)
                .append("status", status)
                .toString();
    }
}
