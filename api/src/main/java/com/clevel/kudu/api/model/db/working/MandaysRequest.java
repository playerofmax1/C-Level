package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.converter.DurationConverter;
import com.clevel.kudu.api.model.db.AbstractAuditEntity;
import com.clevel.kudu.model.RequestStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Table(name = "wrk_mandays_request")
public class MandaysRequest extends AbstractAuditEntity {

    @Id
    @Column(name = "userId")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prjTaskId")
    private ProjectTask projectTask;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId")
    private Task task;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "extendMD")
    private BigDecimal extendMD;

    @Column(name = "extendMDDuration")
    @Convert(converter = DurationConverter.class)
    private Duration extendMDDuration;

    @Column(name = "extendMDMinute")
    private Long extendMDMinute;

    @Column(name = "description")
    private String description;

    @Column(name = "amdCalculation")
    private boolean amdCalculation;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public ProjectTask getProjectTask() {
        return projectTask;
    }

    public void setProjectTask(ProjectTask projectTask) {
        this.projectTask = projectTask;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
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

    public boolean isAmdCalculation() {
        return amdCalculation;
    }

    public void setAmdCalculation(boolean amdCalculation) {
        this.amdCalculation = amdCalculation;
    }
}
