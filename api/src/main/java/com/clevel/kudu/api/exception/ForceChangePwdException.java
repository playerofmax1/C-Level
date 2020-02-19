package com.clevel.kudu.api.exception;

import com.clevel.kudu.dto.security.AuthenticationResult;

public class ForceChangePwdException extends Exception {
    AuthenticationResult result;

    public ForceChangePwdException(String message, AuthenticationResult result) {
        super(message);
        this.result = result;
    }

    public AuthenticationResult getResult() {
        return result;
    }

    public void setResult(AuthenticationResult result) {
        this.result = result;
    }
}
