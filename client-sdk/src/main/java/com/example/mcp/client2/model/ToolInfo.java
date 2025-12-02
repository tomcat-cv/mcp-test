package com.example.mcp.client2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * MCP工具信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolInfo {

    /**
     * 工具名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 工具描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 工具参数Schema（JSON格式）
     */
    @JsonProperty("inputSchema")
    private Object inputSchema;
}

