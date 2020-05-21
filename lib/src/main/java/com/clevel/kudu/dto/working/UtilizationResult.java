package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UtilizationResult {
    UtilizationDTO utilization;

    public UtilizationResult() {
    }

    public UtilizationResult(UtilizationDTO utilization) {
        this.utilization = utilization;
    }

    public UtilizationDTO getUtilization() {
        return utilization;
    }

    public void setUtilization(UtilizationDTO utilization) {
        this.utilization = utilization;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("utilization", utilization)
                .toString();
    }
}
