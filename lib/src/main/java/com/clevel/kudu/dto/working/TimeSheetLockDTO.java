package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.LookupList;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class TimeSheetLockDTO implements LookupList {

    private long id;
    private UserDTO user;
    private Date startDate;
    private Date endDate;

    public TimeSheetLockDTO() {
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
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
