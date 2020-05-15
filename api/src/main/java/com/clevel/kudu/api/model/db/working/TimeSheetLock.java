package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.db.AbstractAuditEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wrk_timesheet_lock")
public class TimeSheetLock extends AbstractAuditEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId")
  private User user;

  @Column(name = "startDate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date startDate;

  @Column(name = "endDate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date endDate;
  
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
            .append("id", id)
            .append("user", user)
            .append("startDate", startDate)
            .append("endDate", endDate)
            .toString()
            .replace('=', ':');
  }
}
