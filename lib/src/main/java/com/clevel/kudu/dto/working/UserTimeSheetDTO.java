package com.clevel.kudu.dto.working;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserTimeSheetDTO {
    private long id;
    private UserDTO user;
    private UserDTO timeSheetUser;

    public UserTimeSheetDTO() {
    }

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

    public UserDTO getTimeSheetUser() {
        return timeSheetUser;
    }

    public void setTimeSheetUser(UserDTO timeSheetUser) {
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
