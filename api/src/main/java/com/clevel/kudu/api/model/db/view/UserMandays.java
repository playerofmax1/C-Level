package com.clevel.kudu.api.model.db.view;


import com.clevel.kudu.api.model.db.working.Project;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "vw_rpt_mandays")
public class UserMandays implements Serializable {

  @Id
  @Column(name = "userId")
  private long userId;

  @Id
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "projectId")
  private Project project;

  @Id
  @Column(name = "workYear")
  private long workYear;



  @Column(name = "targetPercentCU")
  private BigDecimal targetPercentCU;

  @Column(name = "chargeMinutes")
  private long chargeMinutes;

  @Column(name = "chargeHours")
  private BigDecimal chargeHours;

  @Column(name = "chargeDays")
  private BigDecimal chargeDays;

  @Column(name = "workDays")
  private long workDays;

  @Column(name = "firstChargeDate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date firstChargeDate;

  @Column(name = "lastChargeDate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastChargeDate;

  @Column(name = "firstToLastDays")
  private long firstToLastDays;

  @Column(name = "PMD")
  private BigDecimal PMD;

  @Column(name = "AMD")
  private BigDecimal AMD;

  @Column(name = "RPMDPercent")
  private BigDecimal RPMDPercent;

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public long getWorkYear() {
    return workYear;
  }

  public void setWorkYear(long workYear) {
    this.workYear = workYear;
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
}
