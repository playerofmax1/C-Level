package com.clevel.kudu.api.model.db.relation;

import com.clevel.kudu.api.model.db.AbstractEntity;
import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.model.Function;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "rel_role_function")
public class RelRoleFunction extends AbstractEntity {
    @OneToOne
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;
    @Column(name = "function")
    @Enumerated(EnumType.STRING)
    private Function function;

    public RelRoleFunction() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("role", role)
                .append("function", function)
                .toString();
    }
}
