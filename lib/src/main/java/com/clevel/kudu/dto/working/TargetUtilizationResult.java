package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TargetUtilizationResult {

    private UserPerformanceDTO userPerformance;

    public UserPerformanceDTO getUserPerformance() {
        return userPerformance;
    }

    public void setUserPerformance(UserPerformanceDTO userPerformance) {
        this.userPerformance = userPerformance;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userPerformance", userPerformance)
                .toString()
                .replace('=', ':');
    }
}
