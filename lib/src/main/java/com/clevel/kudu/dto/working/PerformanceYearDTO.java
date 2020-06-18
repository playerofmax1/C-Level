package com.clevel.kudu.dto.working;

import com.clevel.kudu.util.DateTimeUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class PerformanceYearDTO {
    private long year;
    private Date startDate;
    private Date endDate;

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDisplayString() {
        return "Performance Year : " + year + " from " + DateTimeUtil.getDateStr(startDate) + " to " + DateTimeUtil.getDateStr(endDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("year", year)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .toString()
                .replace('=', ':');
    }
}
