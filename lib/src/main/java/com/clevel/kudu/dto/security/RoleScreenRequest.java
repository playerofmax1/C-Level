package com.clevel.kudu.dto.security;

import com.clevel.kudu.model.Screen;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class RoleScreenRequest {
    private RoleDTO role;
    private List<Screen> screenList;

    public RoleScreenRequest() {
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }

    public List<Screen> getScreenList() {
        return screenList;
    }

    public void setScreenList(List<Screen> screenList) {
        this.screenList = screenList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("role", role)
                .append("screenList", screenList)
                .toString();
    }
}
