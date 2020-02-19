package com.clevel.kudu.model;

public enum RecordStatus {
    // normal status
    INACTIVE(0),
    ACTIVE(1),
    LOCK(2),
    CLOSE(3),

    FORCE_CHANGE_PWD(10);

    int code;

    RecordStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
