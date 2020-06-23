package com.clevel.kudu.dto.rpt;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class MandaysReportResult {

    private List<String> projectList;
    private List<MandaysReportItem> reportItemList;

    public List<String> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<String> projectList) {
        this.projectList = projectList;
    }

    public List<MandaysReportItem> getReportItemList() {
        return reportItemList;
    }

    public void setReportItemList(List<MandaysReportItem> reportItemList) {
        this.reportItemList = reportItemList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("projectList", projectList)
                .append("reportItemList", reportItemList)
                .toString()
                .replace('=', ':');
    }
}
