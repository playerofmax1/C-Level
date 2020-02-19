package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class CostDTO {
    private BigDecimal planCost;
    private BigDecimal extendCost;
    private BigDecimal totalCost;
    private BigDecimal actualCost;

    public CostDTO() {
    }

    public BigDecimal getPlanCost() {
        return planCost;
    }

    public void setPlanCost(BigDecimal planCost) {
        this.planCost = planCost;
    }

    public BigDecimal getExtendCost() {
        return extendCost;
    }

    public void setExtendCost(BigDecimal extendCost) {
        this.extendCost = extendCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("planCost", planCost)
                .append("extendCost", extendCost)
                .append("totalCost", totalCost)
                .append("actualCost", actualCost)
                .toString();
    }
}
