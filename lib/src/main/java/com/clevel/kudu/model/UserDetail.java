package com.clevel.kudu.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Used as a Session Object to store logged-in user information.
 * All variables needed for the Session can put here without any changes to the Authentication.
 */
public class UserDetail implements Serializable {
    private long userId;
    private String userName;
    private String name;
    private String lastName;
    private String email;
    private String role;
    private Date tsStartDate;

    private List<Screen> screenList;
    private List<Function> functionList;

    public UserDetail() {
    }

    public UserDetail(long userId, String userName, String email, String role) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getTsStartDate() {
        return tsStartDate;
    }

    public void setTsStartDate(Date tsStartDate) {
        this.tsStartDate = tsStartDate;
    }

    public List<Screen> getScreenList() {
        return screenList;
    }

    public void setScreenList(List<Screen> screenList) {
        this.screenList = screenList;
    }

    public List<Function> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<Function> functionList) {
        this.functionList = functionList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("userId", userId)
                .append("userName", userName)
                .append("name", name)
                .append("lastName", lastName)
                .append("email", email)
                .append("role", role)
                .append("tsStartDate", tsStartDate)
                .append("screenList", screenList)
                .append("functionList", functionList)
                .toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof UserDetail) {
            final UserDetail other = (UserDetail) obj;
            return new EqualsBuilder()
                    .append(userId, other.userId)
                    .append(userName, other.userName)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(userId)
                .append(userName)
                .toHashCode();
    }

}
