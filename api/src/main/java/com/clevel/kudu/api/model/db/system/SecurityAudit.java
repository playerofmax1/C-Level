package com.clevel.kudu.api.model.db.system;

import com.clevel.kudu.api.model.db.AbstractEntity;
import com.clevel.kudu.util.DateTimeUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sec_audit")
public class SecurityAudit extends AbstractEntity {
    @Column(name = "actionDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDate;
    @Column(name = "requestURL")
    private String requestURL;
    @Column(name = "clientIP")
    private String clientIP;
    @Column(name = "userAgent")
    private String userAgent;
    @Column(name = "referer")
    private String referer;
    @Column(name = "sessionId")
    private String sessionId;
    @Column(name = "request", length = 1000)
    private String request;

    public SecurityAudit() {
    }

    public SecurityAudit(String requestURL, String clientIP, String userAgent, String referer, String sessionId, String request) {
        this.actionDate = DateTimeUtil.now();
        this.requestURL = requestURL;
        this.clientIP = clientIP;
        this.userAgent = userAgent;
        this.referer = referer;
        this.sessionId = sessionId;
        this.request = request;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("requestURL", requestURL)
                .append("clientIP", clientIP)
                .append("userAgent", userAgent)
                .append("referer", referer)
                .append("sessionId", sessionId)
                .append("request", request)
                .toString();
    }
}
