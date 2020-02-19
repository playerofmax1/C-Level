package com.clevel.kudu.dto;

import com.clevel.kudu.model.APIResponse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ServiceResponse<T> {
    private APIResponse apiResponse;
    private String message;
    private T result;

    public ServiceResponse() {
    }

    public ServiceResponse(APIResponse apiResponse, String message) {
        this.apiResponse = apiResponse;
        this.message = message;
    }

    public ServiceResponse(APIResponse apiResponse) {
        this.apiResponse = apiResponse;
    }

    public APIResponse getApiResponse() {
        return apiResponse;
    }

    public void setApiResponse(APIResponse apiResponse) {
        this.apiResponse = apiResponse;
        this.message = "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("apiResponse", apiResponse)
                .append("message", message)
                .append("result", result)
                .toString();
    }
}
