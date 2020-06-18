package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class TimeSheetResult {
    private List<TimeSheetDTO> timeSheetList;
    private List<TimeSheetLockDTO> timeSheetLockList;
    private UtilizationDTO utilization;
    private boolean cutoffEnable;
    private int cutoffDate;

    private boolean hasPreviousMonth;
    private boolean hasNextMonth;

    public TimeSheetResult() {
    }

    public List<TimeSheetDTO> getTimeSheetList() {
        return timeSheetList;
    }

    public void setTimeSheetList(List<TimeSheetDTO> timeSheetList) {
        this.timeSheetList = timeSheetList;
    }

    public List<TimeSheetLockDTO> getTimeSheetLockList() {
        return timeSheetLockList;
    }

    public void setTimeSheetLockList(List<TimeSheetLockDTO> timeSheetLockList) {
        this.timeSheetLockList = timeSheetLockList;
    }

    public UtilizationDTO getUtilization() {
        return utilization;
    }

    public void setUtilization(UtilizationDTO utilization) {
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

    public boolean isHasNextMonth() {
        return hasNextMonth;
    }

    public void setHasNextMonth(boolean hasNextMonth) {
        this.hasNextMonth = hasNextMonth;
    }

    public boolean isHasPreviousMonth() {
        return hasPreviousMonth;
    }

    public void setHasPreviousMonth(boolean hasPreviousMonth) {
        this.hasPreviousMonth = hasPreviousMonth;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("timeSheetList", timeSheetList)
                .append("timeSheetLockList", timeSheetLockList)
                .append("utilization", utilization)
                .append("cutoffEnable", cutoffEnable)
                .append("cutoffDate", cutoffDate)
                .append("hasPreviousMonth", hasPreviousMonth)
                .append("hasNextMonth", hasNextMonth)
                .toString()
                .replace('=', ':');
    }
}
