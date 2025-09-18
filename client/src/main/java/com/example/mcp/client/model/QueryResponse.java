package com.example.mcp.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("result")
    private String result;
    
    public QueryResponse() {}
    
    public QueryResponse(boolean success, String message, String result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
}
