package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.LookupList;
import com.clevel.kudu.model.SystemConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ConfigDTO implements LookupList {
    private long id;

    private SystemConfig systemConfig;
    private String name;
    private String value;
    private String description;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
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
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("systemConfig", systemConfig)
                .append("name", name)
                .append("value", value)
                .append("description", description)
                .toString()
                .replace('=', ':');
    }
}
