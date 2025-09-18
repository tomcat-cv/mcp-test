package com.example.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "downstream")
public class DownstreamProperties {
	private String baseUrl;
	private String path = "/query";
	private String apiKey;
	private String apiKeyHeader = "Authorization";

	public String getBaseUrl() { return baseUrl; }
	public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }

	public String getApiKey() { return apiKey; }
	public void setApiKey(String apiKey) { this.apiKey = apiKey; }

	public String getApiKeyHeader() { return apiKeyHeader; }
	public void setApiKeyHeader(String apiKeyHeader) { this.apiKeyHeader = apiKeyHeader; }
}
