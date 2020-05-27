package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class TargetUtilizationRequest {
    private long userId;
    private int year;
    private BigDecimal targetUtilization;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
                .append("year", year)
                .append("targetUtilization", targetUtilization)
                .toString()
                .replace('=', ':');
    }
}
