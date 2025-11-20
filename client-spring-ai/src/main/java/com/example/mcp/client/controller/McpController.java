package com.example.mcp.client.controller;

import com.example.mcp.client.model.QueryRequest;
import com.example.mcp.client.model.QueryResponse;
import com.example.mcp.model.StandardQueryResponse;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mcp")
public class McpController {

    @Autowired
    private ChatClient chatClient;

    @PostMapping("/query")
    public ResponseEntity<QueryResponse> query(@RequestBody QueryRequest request) {
        try {
            System.out.println("请求: " + request.getQuery());

            StandardQueryResponse standardQueryResponse = chatClient.prompt(request.getQuery()).call().entity(StandardQueryResponse.class);
            System.out.println("回复: " + standardQueryResponse);

            if (standardQueryResponse != null) {
                return ResponseEntity.ok(new QueryResponse(true, "查询成功", standardQueryResponse.getMessage()));
            } else {
                return ResponseEntity.ok(new QueryResponse(false, "查询失败"));
            }
        } catch (Exception e) {
            System.err.println("查询失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new QueryResponse(false, "查询失败: " + e.getMessage(), null));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("MCP Client is running");
    }
}
