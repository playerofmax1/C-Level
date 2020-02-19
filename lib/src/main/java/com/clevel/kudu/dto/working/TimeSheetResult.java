package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.List;

public class TimeSheetResult {
    private List<TimeSheetDTO> timeSheetList;
    private BigDecimal utilization;
    private boolean cutoffEnable;
    private int cutoffDate;

    public TimeSheetResult() {
    }

    public List<TimeSheetDTO> getTimeSheetList() {
        return timeSheetList;
    }

    public void setTimeSheetList(List<TimeSheetDTO> timeSheetList) {
        this.timeSheetList = timeSheetList;
    }

    public BigDecimal getUtilization() {
        return utilization;
    }

    public void setUtilization(BigDecimal utilization) {
        this.utilization = utilization;
    }

    public boolean isCutoffEnable() {
        return cutoffEnable;
    }

    public void setCutoffEnable(boolean cutoffEnable) {
        this.cutoffEnable = cutoffEnable;
    }

    public int getCutoffDate() {
        return cutoffDate;
    }

    public void setCutoffDate(int cutoffDate) {
        this.cutoffDate = cutoffDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("timeSheetList", timeSheetList)
                .append("utilization", utilization)
                .append("cutoffEnable", cutoffEnable)
                .append("cutoffDate", cutoffDate)
                .toString();
    }
}
