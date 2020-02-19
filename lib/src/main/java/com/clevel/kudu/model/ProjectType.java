package com.clevel.kudu.model;

public enum ProjectType {
    NORMAL(1),MAINTENANCE(2);

    int code;

    ProjectType(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
