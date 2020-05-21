package com.clevel.kudu.dto.working;

import com.clevel.kudu.model.RecordStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SearchRequest {
    String code;
    String name;
    RecordStatus status;

    public SearchRequest() {
        this.code="";
        this.name="";
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

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("code", code)
                .append("name", name)
                .append("status", status)
                .toString()
                .replace('=', ':');
    }
}
