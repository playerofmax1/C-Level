package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.RequestStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserStatusRequest {
    private long userId;
    private RequestStatus status;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userId", userId)
                .append("status", status)
                .toString()
                .replace('=', ':');
    }
}
