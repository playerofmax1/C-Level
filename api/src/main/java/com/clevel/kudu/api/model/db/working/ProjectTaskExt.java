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

@Entity
@Table(name = "wrk_prj_task_ext")
public class ProjectTaskExt extends AbstractAuditEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", nullable = false)
    private ProjectTask parent;

    @Column(name = "extendMD")
    private BigDecimal extendMD;
    @Column(name = "extendMDDuration")
    @Convert(converter = DurationConverter.class)
    private Duration extendMDDuration;
    @Column(name = "extendMDMinute")
    private Long extendMDMinute;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Convert(converter = RecordStatusConverter.class)
    private RecordStatus status;

    public ProjectTaskExt() {
    }

    public ProjectTask getParent() {
        return parent;
    }

    public void setParent(ProjectTask parent) {
        this.parent = parent;
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
                .append("parent", parent)
                .append("extendMD", extendMD)
                .append("extendMDDuration", extendMDDuration)
                .append("extendMDMinute", extendMDMinute)
                .append("description", description)
                .append("status", status)
                .toString();
    }
}
