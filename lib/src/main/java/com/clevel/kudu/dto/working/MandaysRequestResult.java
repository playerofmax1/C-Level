package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class MandaysRequestResult {

    private PerformanceYearDTO performanceYear;
    private List<MandaysRequestDTO> mandaysRequestList;

    private boolean hasPreviousYear;
    private boolean hasNextYear;

    public PerformanceYearDTO getPerformanceYear() {
        return performanceYear;
    }

    public void setPerformanceYear(PerformanceYearDTO performanceYear) {
        this.performanceYear = performanceYear;
    }

    public List<MandaysRequestDTO> getMandaysRequestList() {
        return mandaysRequestList;
    }

    public void setMandaysRequestList(List<MandaysRequestDTO> mandaysRequestList) {
        this.mandaysRequestList = mandaysRequestList;
    }

    public boolean isHasPreviousYear() {
        return hasPreviousYear;
    }

    public void setHasPreviousYear(boolean hasPreviousYear) {
        this.hasPreviousYear = hasPreviousYear;
    }

    public boolean isHasNextYear() {
        return hasNextYear;
    }

    public void setHasNextYear(boolean hasNextYear) {
        this.hasNextYear = hasNextYear;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("performanceYear", performanceYear)
                .append("mandaysRequestList", mandaysRequestList)
                .append("hasPreviousYear", hasPreviousYear)
                .append("hasNextYear", hasNextYear)
                .toString()
                .replace('=', ':');
    }
}
