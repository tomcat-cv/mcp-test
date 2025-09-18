package com.example.mcp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class StandardQueryRequest {
	@NotBlank
	private String query;

	@NotBlank
	private String locale = "zh-CN";

	private Map<String, Object> metadata;

	@NotNull
	private Long timestamp;

	public String getQuery() { return query; }
	public void setQuery(String query) { this.query = query; }

	public String getLocale() { return locale; }
	public void setLocale(String locale) { this.locale = locale; }

	public Map<String, Object> getMetadata() { return metadata; }
	public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

	public Long getTimestamp() { return timestamp; }
	public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
