package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class TimeSheetRequest {
    private long timeSheetUserId;
    private Date month;

    public TimeSheetRequest() {
    }

    public long getTimeSheetUserId() {
        return timeSheetUserId;
    }

    public void setTimeSheetUserId(long timeSheetUserId) {
        this.timeSheetUserId = timeSheetUserId;
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
                .append("timeSheetUserId", timeSheetUserId)
                .append("month", month)
                .toString();
    }
}
