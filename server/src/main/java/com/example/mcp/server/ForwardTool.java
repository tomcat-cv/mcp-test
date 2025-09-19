package com.example.mcp.server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;
import com.example.mcp.service.ForwardService;

import reactor.core.publisher.Flux;

@Service
public class ForwardTool {
    private final ForwardService forwardService;

    public ForwardTool(ForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @Tool(name = "forward_query_stream", description = "将标准化输入转发到下游并返回流式事件")
    public Flux<StandardQueryResponse> forwardStream(StandardQueryRequest request) {
        System.out.println("[ForwardTool] 收到流式请求: request='" + request + "'");
        return forwardService.forwardStream(request)
                .doOnError(ex -> {
                    System.err.println("[ForwardTool] 流式处理失败: " + ex.getMessage());
                    ex.printStackTrace();
                });
    }
}