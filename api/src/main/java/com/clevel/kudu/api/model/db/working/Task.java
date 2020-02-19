package com.clevel.kudu.api.model.db.working;

import com.clevel.kudu.api.model.converter.RecordStatusConverter;
import com.clevel.kudu.api.model.converter.TaskTypeConverter;
import com.clevel.kudu.api.model.db.AbstractAuditEntity;
import com.clevel.kudu.model.RecordStatus;
import com.clevel.kudu.model.TaskType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "wrk_task")
public class Task extends AbstractAuditEntity {
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "chargeable")
    private boolean chargeable;

    @Column(name = "type")
    @Convert(converter = TaskTypeConverter.class)
    private TaskType type;

    @Column(name = "status")
    @Convert(converter = RecordStatusConverter.class)
    private RecordStatus status;

    public Task() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChargeable() {
        return chargeable;
    }

    public void setChargeable(boolean chargeable) {
        this.chargeable = chargeable;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
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
                .append("code", code)
                .append("name", name)
                .append("description", description)
                .append("chargeable", chargeable)
                .append("type", type)
                .append("status", status)
                .toString();
    }
}
