package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

public class UserMandaysDTO {

    private long userId;
    private ProjectDTO project;
    private long workYear;

    private boolean planFlag;
    private BigDecimal targetPercentCU;

    private long chargeMinutes;
    private BigDecimal chargeHours;
    private BigDecimal chargeDays;
    private long workDays;

    private Date firstChargeDate;
    private Date lastChargeDate;
    private long firstToLastDays;

    private BigDecimal PMD;
    private BigDecimal AMD;
    private BigDecimal RPMDPercent;

    private long netWorkdays;
    private BigDecimal weight;

    private String styleClass;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public long getWorkYear() {
        return workYear;
    }

    public void setWorkYear(long workYear) {
        this.workYear = workYear;
    }

    public boolean isPlanFlag() {
        return planFlag;
    }

    public void setPlanFlag(boolean planFlag) {
        this.planFlag = planFlag;
    }

    public BigDecimal getTargetPercentCU() {
        return targetPercentCU;
    }

    public void setTargetPercentCU(BigDecimal targetPercentCU) {
        this.targetPercentCU = targetPercentCU;
    }

    public long getChargeMinutes() {
        return chargeMinutes;
    }

    public void setChargeMinutes(long chargeMinutes) {
        this.chargeMinutes = chargeMinutes;
    }

    public BigDecimal getChargeHours() {
        return chargeHours;
    }

    public void setChargeHours(BigDecimal chargeHours) {
        this.chargeHours = chargeHours;
    }

    public BigDecimal getChargeDays() {
        return chargeDays;
    }

    public void setChargeDays(BigDecimal chargeDays) {
        this.chargeDays = chargeDays;
    }

    public long getWorkDays() {
        return workDays;
    }

    public void setWorkDays(long workDays) {
        this.workDays = workDays;
    }

    public Date getFirstChargeDate() {
        return firstChargeDate;
    }

    public void setFirstChargeDate(Date firstChargeDate) {
        this.firstChargeDate = firstChargeDate;
    }

    public Date getLastChargeDate() {
        return lastChargeDate;
    }

    public void setLastChargeDate(Date lastChargeDate) {
        this.lastChargeDate = lastChargeDate;
    }

    public long getFirstToLastDays() {
        return firstToLastDays;
    }

    public void setFirstToLastDays(long firstToLastDays) {
        this.firstToLastDays = firstToLastDays;
    }

    public BigDecimal getPMD() {
        return PMD;
    }

    public void setPMD(BigDecimal PMD) {
        this.PMD = PMD;
    }

    public BigDecimal getAMD() {
        return AMD;
    }

    public void setAMD(BigDecimal AMD) {
        this.AMD = AMD;
    }

    public BigDecimal getRPMDPercent() {
        return RPMDPercent;
    }

    public void setRPMDPercent(BigDecimal RPMDPercent) {
        this.RPMDPercent = RPMDPercent;
    }

    public long getNetWorkdays() {
        return netWorkdays;
    }

    public void setNetWorkdays(long netWorkdays) {
        this.netWorkdays = netWorkdays;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getStyleClass() {
        if (styleClass == null) {
            if (project == null) {
                styleClass = "noProjectLabel";
            } else if (!planFlag) {
                styleClass = "nonPlanLabel";
            } else {
                styleClass = "";
            }
        }
        return styleClass;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userId", userId)
                .append("project", project)
                .append("workYear", workYear)
                .append("planFlag", planFlag)
                .append("targetPercentCU", targetPercentCU)
                .append("chargeMinutes", chargeMinutes)
                .append("chargeHours", chargeHours)
                .append("chargeDays", chargeDays)
                .append("workDays", workDays)
                .append("firstChargeDate", firstChargeDate)
                .append("lastChargeDate", lastChargeDate)
                .append("firstToLastDays", firstToLastDays)
                .append("PMD", PMD)
                .append("AMD", AMD)
                .append("RPMDPercent", RPMDPercent)
                .append("netWorkdays", netWorkdays)
                .append("weight", weight)
                .append("styleClass", styleClass)
                .toString()
                .replace('=', ':');
    }
}
