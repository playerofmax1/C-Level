package com.clevel.kudu.dto.rpt;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.List;

public class MandaysReportItem {
    private String name;
    private BigDecimal targetPercentCU;
    private BigDecimal percentCU;
    private BigDecimal finalPercentAMD;

    private List<BigDecimal> amdList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTargetPercentCU() {
        return targetPercentCU;
    }

    public void setTargetPercentCU(BigDecimal targetPercentCU) {
        this.targetPercentCU = targetPercentCU;
    }

    public List<BigDecimal> getAmdList() {
        return amdList;
    }

    public void setAmdList(List<BigDecimal> amdList) {
        this.amdList = amdList;
    }

    public BigDecimal getPercentCU() {
        return percentCU;
    }

    public void setPercentCU(BigDecimal percentCU) {
        this.percentCU = percentCU;
    }

    public BigDecimal getFinalPercentAMD() {
        return finalPercentAMD;
    }

    public void setFinalPercentAMD(BigDecimal finalPercentAMD) {
        this.finalPercentAMD = finalPercentAMD;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("name", name)
                .append("targetPercentCU", targetPercentCU)
                .append("percentCU", percentCU)
                .append("finalPercentAMD", finalPercentAMD)
                .append("amdList", amdList)
                .toString()
                .replace('=', ':');
    }
}
