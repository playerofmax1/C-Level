package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class UtilizationRequest {
    private long requestUserId;
    private Date month;
    private long totalChargedMinutes;

    public UtilizationRequest() {
    }

    public UtilizationRequest(long requestUserId, Date month) {
        this.requestUserId = requestUserId;
        this.month = month;
    }

    public long getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public long getTotalChargedMinutes() {
        return totalChargedMinutes;
    }

    public void setTotalChargedMinutes(long totalChargedMinutes) {
        this.totalChargedMinutes = totalChargedMinutes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("requestUserId", requestUserId)
                .append("month", month)
                .append("totalChargedMinutes", totalChargedMinutes)
                .toString()
                .replace('=', ':');
    }
}
