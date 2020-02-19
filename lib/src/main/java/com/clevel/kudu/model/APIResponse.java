package com.clevel.kudu.model;

public enum APIResponse {
    // Operations
    SUCCESS(100,"Success"),
    FAILED(101,"Failed!"),
    EXCEPTION(102,"Exception!"),

    // Validations
    INVALID_INPUT_PARAMETER(201,"Invalid input parameter"),
    RECORD_EXIST(202,"Record already exist"),
    RECORD_UPDATED_BY_OTHER_SESSION(203,"Record updated by other session"),
    APPROVAL_REQUIRED(204,"Approval required"),
    LOGIN_NAME_ALREADY_IN_USE(205,"Login name is already in use"),
    ACCOUNT_DISABLED(206,"Account disabled"),
    CODE_ALREADY_IN_USE(207,"Code is already in use"),
    ROLE_CURRENTLY_IN_USE(208,"Role currently in use"),
    MD_IS_OVER_LIMIT(209,"Man-day is over limit."),

    // Force Operations
    FORCE_CHANGE_PWD(301,"Force change password"),
    ;

    int code;
    String description;

    APIResponse(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
