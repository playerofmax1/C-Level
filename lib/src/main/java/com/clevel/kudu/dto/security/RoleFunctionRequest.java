package com.clevel.kudu.dto.security;

import com.clevel.kudu.model.Function;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class RoleFunctionRequest {
    private RoleDTO role;
    private List<Function> functionList;

    public RoleFunctionRequest() {
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
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
                .append("role", role)
                .append("functionList", functionList)
                .toString();
    }
}
