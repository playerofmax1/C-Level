package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.LookupList;
import com.clevel.kudu.model.ProjectType;
import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.Duration;

public class ProjectDTO implements LookupList {
    private long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal manDays;

    private BigDecimal billableMD;
    private Duration billableMDDuration;
    private Long billableMDMinute;

    private BigDecimal totalPlanMD;
    private Duration totalPlanMDDuration;
    private Long totalPlanMDMinute;

    private BigDecimal remainingMD;
    private Duration remainingMDDuration;
    private Long remainingMDMinute;

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
        if (billableMDDuration == null) {
            billableMDDuration = Duration.ZERO;
            billableMD = BigDecimal.ZERO;
            billableMDMinute = 0L;
        }
        this.billableMDDuration = billableMDDuration;
    }

    public boolean isMoreThan99Hours(){
        if (billableMDDuration == null) {
            return false;
        }

        return billableMDDuration.toHours() > 99;
    }

    public Long getBillableMDMinute() {
        return billableMDMinute;
    }

    public void setBillableMDMinute(Long billableMDMinute) {
        this.billableMDMinute = billableMDMinute;
    }

    public BigDecimal getTotalPlanMD() {
        return totalPlanMD;
    }

    public void setTotalPlanMD(BigDecimal totalPlanMD) {
        this.totalPlanMD = totalPlanMD;
        this.remainingMD = billableMD.subtract(this.totalPlanMD);
        /*TODO: all totalPlanMD fields and remainingMD fields may be need value too*/
    }

    public Duration getTotalPlanMDDuration() {
        return totalPlanMDDuration;
    }

    public void setTotalPlanMDDuration(Duration totalPlanMDDuration) {
        this.totalPlanMDDuration = totalPlanMDDuration;
    }

    public Long getTotalPlanMDMinute() {
        return totalPlanMDMinute;
    }

    public void setTotalPlanMDMinute(Long totalPlanMDMinute) {
        this.totalPlanMDMinute = totalPlanMDMinute;
    }

    public BigDecimal getRemainingMD() {
        return remainingMD;
    }

    public void setRemainingMD(BigDecimal remainingMD) {
        this.remainingMD = remainingMD;
    }

    public Duration getRemainingMDDuration() {
        return remainingMDDuration;
    }

    public void setRemainingMDDuration(Duration remainingMDDuration) {
        this.remainingMDDuration = remainingMDDuration;
    }

    public Long getRemainingMDMinute() {
        return remainingMDMinute;
    }

    public void setRemainingMDMinute(Long remainingMDMinute) {
        this.remainingMDMinute = remainingMDMinute;
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
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("code", code)
                .append("name", name)
                .append("description", description)
                .append("manDays", manDays)
                .append("billableMD", billableMD)
                .append("billableMDDuration", billableMDDuration)
                .append("billableMDMinute", billableMDMinute)
                .append("totalPlanMD", totalPlanMD)
                .append("totalPlanMDDuration", totalPlanMDDuration)
                .append("totalPlanMDMinute", totalPlanMDMinute)
                .append("remainingMD", remainingMD)
                .append("remainingMDDuration", remainingMDDuration)
                .append("remainingMDMinute", remainingMDMinute)
                .append("customer", customer)
                .append("type", type)
                .append("status", status)
                .append("version", version)
                .toString()
                .replace('=', ':');
    }
}
