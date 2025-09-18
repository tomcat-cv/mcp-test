package com.example.mcp.model;

import java.util.Map;

public class DownstreamRequest {
	private String q;
	private String lang;
	private Map<String, Object> extra;
	private Long ts;

	public DownstreamRequest() {}

	public DownstreamRequest(String q, String lang, Map<String, Object> extra, Long ts) {
		this.q = q;
		this.lang = lang;
		this.extra = extra;
		this.ts = ts;
	}

	public String getQ() { return q; }
	public void setQ(String q) { this.q = q; }

	public String getLang() { return lang; }
	public void setLang(String lang) { this.lang = lang; }

	public Map<String, Object> getExtra() { return extra; }
	public void setExtra(Map<String, Object> extra) { this.extra = extra; }

	public Long getTs() { return ts; }
	public void setTs(Long ts) { this.ts = ts; }
}
