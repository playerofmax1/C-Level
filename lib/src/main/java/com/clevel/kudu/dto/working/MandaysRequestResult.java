package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class MandaysRequestResult {

    private PerformanceYearDTO performanceYear;
    private List<MandaysRequestDTO> mandaysRequestList;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("performanceYear", performanceYear)
                .append("mandaysRequestList", mandaysRequestList)
                .toString()
                .replace('=', ':');
    }
}
