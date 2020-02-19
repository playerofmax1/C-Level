package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.converter.RecordStatusConverter;
import com.clevel.kudu.api.model.db.AbstractAuditEntity;
import com.clevel.kudu.api.model.db.master.Rate;
import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "wrk_user")
public class User extends AbstractAuditEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "loginName", length = 50)
    private String loginName;
    @Column(name = "pwd",nullable = false)
    private String password;
    @Column(name = "email", length = 80)
    private String email;
    @Column(name = "phoneNumber", length = 30)
    private String phoneNumber;
    @Column(name = "lastLoginDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    @Column(name = "tsStartDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tsStartDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rateId")
    private Rate rate;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId")
    private Role role;

    @Column(name = "status")
    @Convert(converter = RecordStatusConverter.class)
    private RecordStatus status;

    public User() {
        status = RecordStatus.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getTsStartDate() {
        return tsStartDate;
    }

    public void setTsStartDate(Date tsStartDate) {
        this.tsStartDate = tsStartDate;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("name", name)
                .append("lastName", lastName)
                .append("loginName", loginName)
                .append("email", email)
                .append("phoneNumber", phoneNumber)
                .append("lastLoginDate", lastLoginDate)
                .append("tsStartDate", tsStartDate)
                .append("rate", rate)
                .append("role", role)
                .append("status", status)
                .toString();
    }
}
