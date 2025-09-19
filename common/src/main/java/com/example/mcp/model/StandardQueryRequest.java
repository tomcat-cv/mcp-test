package com.example.mcp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StandardQueryRequest {
	@NotBlank
	private String seqNo;

	@NotBlank
	private String systemCode;

	@NotNull
	private Long timestamp;

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

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "StandardQueryRequest [seqNo=" + seqNo + ", systemCode=" + systemCode + ", timestamp=" + timestamp + "]";
	}
}
