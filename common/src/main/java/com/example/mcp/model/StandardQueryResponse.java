package com.example.mcp.model;

import java.util.Map;

public class StandardQueryResponse {
	private boolean success;
	private String message;
	private Map<String, Object> result;

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

	public Map<String, Object> getResult() {
		return result;
	}

	public void setResult(Map<String, Object> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "StandardQueryResponse [success=" + success + ", message=" + message + ", result=" + result + "]";
	}
}
