package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.LookupList;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class UserPerformanceDTO implements LookupList {
    private long id;
    private long userId;
    private PerformanceYearDTO performanceYear;

    private BigDecimal targetUtilization;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public PerformanceYearDTO getPerformanceYear() {
        return performanceYear;
    }

    public void setPerformanceYear(PerformanceYearDTO performanceYear) {
        this.performanceYear = performanceYear;
    }

    public BigDecimal getTargetUtilization() {
        return targetUtilization;
    }

    public void setTargetUtilization(BigDecimal targetUtilization) {
        this.targetUtilization = targetUtilization;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userId", userId)
                .append("performanceYear", performanceYear)
                .append("targetUtilization", targetUtilization)
                .toString()
                .replace('=', ':');
    }
}
