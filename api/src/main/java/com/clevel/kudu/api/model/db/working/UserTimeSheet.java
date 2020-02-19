package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.db.AbstractEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "wrk_user_timesheet")
public class UserTimeSheet extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "timesheetUserId", nullable = false)
    private User timeSheetUser;

    public UserTimeSheet() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTimeSheetUser() {
        return timeSheetUser;
    }

    public void setTimeSheetUser(User timeSheetUser) {
        this.timeSheetUser = timeSheetUser;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("user", user)
                .append("timeSheetUser", timeSheetUser)
                .toString();
    }
}
