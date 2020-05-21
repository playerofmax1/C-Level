package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

public class UserMandaysDTO {

  private long userId;
  private ProjectDTO project;

  private long workYear;
  private long chargeMinutes;
  private BigDecimal chargeHours;
  private BigDecimal chargeDays;
  private long workDays;

  private Date firstChargeDate;
  private Date lastChargeDate;
  private long firstToLastDays;

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

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
            .append("userId", userId)
            .append("project", project)
            .append("workYear", workYear)
            .append("chargeMinutes", chargeMinutes)
            .append("chargeHours", chargeHours)
            .append("chargeDays", chargeDays)
            .append("workDays", workDays)
            .append("firstChargeDate", firstChargeDate)
            .append("lastChargeDate", lastChargeDate)
            .append("firstToLastDays", firstToLastDays)
            .toString()
            .replace('=', ':');
  }
}
