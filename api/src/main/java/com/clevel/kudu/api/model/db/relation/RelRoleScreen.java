package com.clevel.kudu.api.model.db.relation;

import com.clevel.kudu.api.model.db.AbstractEntity;
import com.clevel.kudu.api.model.db.security.Role;
import com.clevel.kudu.model.Screen;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "rel_role_screen")
public class RelRoleScreen extends AbstractEntity {
    @OneToOne
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;
    @Column(name = "screen")
    @Enumerated(EnumType.STRING)
    private Screen screen;

    public RelRoleScreen() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("role", role)
                .append("screen", screen)
                .toString();
    }
}
