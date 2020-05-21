package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.db.AbstractAuditEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wrk_performance_year")
public class PerformanceYear extends AbstractAuditEntity {

  @Column(name = "year")
  private long year;

  @Column(name = "startDate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date startDate;

  @Column(name = "endDate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date endDate;

  public long getYear() {
    return year;
  }

  public void setYear(long year) {
    this.year = year;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}
