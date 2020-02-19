package com.clevel.kudu.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ServiceRequest<T> {
    private long userId;
    private T request;

    public ServiceRequest() {
    }

    public ServiceRequest(long userId, T request) {
        this.userId = userId;
        this.request = request;
    }

    public ServiceRequest(T request) {
        this.request = request;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("userId", userId)
                .append("request", request)
                .toString();
    }
}
