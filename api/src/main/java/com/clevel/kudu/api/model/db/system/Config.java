package com.clevel.kudu.api.model.db.system;

import com.clevel.kudu.api.model.db.AbstractEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sys_config")
public class Config extends AbstractEntity {
    @Column(name = "name",unique = true,nullable = false)
    private String name;
    @Column(name = "value",nullable = false)
    private String value;
    @Column(name = "description")
    private String description;

    public Config() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("name", name)
                .append("value", value)
                .append("description", description)
                .toString();
    }
}
