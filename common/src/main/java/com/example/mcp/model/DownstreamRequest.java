package com.example.mcp.model;

import jakarta.validation.constraints.NotBlank;

public class DownstreamRequest {
	@NotBlank
	private String seqNo;

	@NotBlank
	private String systemCode;

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
}
