package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.converter.DurationConverter;
import com.clevel.kudu.api.model.converter.ProjectTypeConverter;
import com.clevel.kudu.api.model.converter.RecordStatusConverter;
import com.clevel.kudu.api.model.db.AbstractAuditEntity;
import com.clevel.kudu.model.ProjectType;
import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Table(name = "wrk_project")
public class Project extends AbstractAuditEntity {
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "manDays")
    private BigDecimal manDays;

    @Column(name = "billableMD")
    private BigDecimal billableMD;
    @Column(name = "billableMDDuration")
    @Convert(converter = DurationConverter.class)
    private Duration billableMDDuration;
    @Column(name = "billableMDMinute")
    private Long billableMDMinute;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "type")
    @Convert(converter = ProjectTypeConverter.class)
    private ProjectType type;

    @Column(name = "status")
    @Convert(converter = RecordStatusConverter.class)
    private RecordStatus status;

    public Project() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getManDays() {
        return manDays;
    }

    public void setManDays(BigDecimal manDays) {
        this.manDays = manDays;
    }

    public BigDecimal getBillableMD() {
        return billableMD;
    }

    public void setBillableMD(BigDecimal billableMD) {
        this.billableMD = billableMD;
    }

    public Duration getBillableMDDuration() {
        return billableMDDuration;
    }

    public void setBillableMDDuration(Duration billableMDDuration) {
        this.billableMDDuration = billableMDDuration;
    }

    public Long getBillableMDMinute() {
        return billableMDMinute;
    }

    public void setBillableMDMinute(Long billableMDMinute) {
        this.billableMDMinute = billableMDMinute;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("code", code)
                .append("name", name)
                .append("description", description)
                .append("manDays", manDays)
                .append("billableMD", billableMD)
                .append("billableMDDuration", billableMDDuration)
                .append("billableMDMinute", billableMDMinute)
                .append("customer", customer)
                .append("type", type)
                .append("status", status)
                .append("id", id)
                .toString()
                .replace('=', ':');
    }
}
