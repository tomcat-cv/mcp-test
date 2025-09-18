package com.example.mcp.model;

import java.util.Map;

public class DownstreamResponse {
	private String status;
	private String message;
	private Map<String, Object> data;

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }

	public Map<String, Object> getData() { return data; }
	public void setData(Map<String, Object> data) { this.data = data; }
}
