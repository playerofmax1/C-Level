package com.clevel.kudu.model;

public enum TaskType {
    GENERAL(1),LEAVE(2);

    int code;

    TaskType(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
