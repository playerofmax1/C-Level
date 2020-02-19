package com.clevel.kudu.api.exception;

import com.clevel.kudu.model.APIResponse;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class ValidationException extends Exception {
    private APIResponse apiResponse;

    public ValidationException(APIResponse apiResponse) {
        super(apiResponse.description());
        this.apiResponse = apiResponse;
    }

    public ValidationException(APIResponse apiResponse, String message) {
        super(message);
        this.apiResponse = apiResponse;
    }

    public APIResponse getApiResponse() {
        return apiResponse;
    }

    public void setApiResponse(APIResponse apiResponse) {
        this.apiResponse = apiResponse;
    }
}
