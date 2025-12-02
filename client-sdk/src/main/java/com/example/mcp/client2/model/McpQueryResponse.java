package com.example.mcp.client2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * MCP查询响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpQueryResponse {

    /**
     * 是否成功
     */
    @JsonProperty("success")
    private boolean success;

    /**
     * 消息
     */
    @JsonProperty("message")
    private String message;

    /**
     * 结果数据
     */
    @JsonProperty("result")
    private Object result;

    public McpQueryResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

