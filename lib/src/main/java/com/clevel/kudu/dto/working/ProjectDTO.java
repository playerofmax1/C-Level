package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.LookupList;
import com.clevel.kudu.model.ProjectType;
import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class ProjectDTO implements LookupList {
    private long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal manDays;

    private CustomerDTO customer;
    private ProjectType type;

    private RecordStatus status;
    private long version;

    public ProjectDTO() {
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("code", code)
                .append("name", name)
                .append("description", description)
                .append("manDays", manDays)
                .append("customer", customer)
                .append("type", type)
                .append("status", status)
                .append("version", version)
                .toString();
    }
}
