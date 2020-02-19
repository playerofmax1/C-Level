package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class UtilizationRequest {
    private long requestUserId;
    private Date month;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("requestUserId", requestUserId)
                .append("month", month)
                .toString();
    }
}
