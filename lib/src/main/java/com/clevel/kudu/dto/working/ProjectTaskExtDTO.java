package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;

public class ProjectTaskExtDTO {
    private ProjectTaskDTO parent;
    private BigDecimal extendMD;
    private Duration extendMDDuration;
    private Long extendMDMinute;
    private String description;
    private RecordStatus status;

    private Date createDate;

    public ProjectTaskExtDTO() {
    }

    public ProjectTaskDTO getParent() {
        return parent;
    }

    public void setParent(ProjectTaskDTO parent) {
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("parent", parent)
                .append("extendMD", extendMD)
                .append("extendMDDuration", extendMDDuration)
                .append("extendMDMinute", extendMDMinute)
                .append("description", description)
                .append("status", status)
                .append("createDate", createDate)
                .toString()
                .replace('=', ':');
    }
}
