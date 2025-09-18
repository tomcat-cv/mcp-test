package com.example.mcp.server;

import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;
import com.example.mcp.service.ForwardService;
import org.springframework.ai.tool.annotation.Tool;
import reactor.core.publisher.Mono;

public class ForwardTool {
    private final ForwardService forwardService;

    public ForwardTool(ForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @Tool(name = "forward_query", description = "将标准化输入转发到下游HTTP服务")
    public Mono<StandardQueryResponse> forward(StandardQueryRequest request) {
        return forwardService.forward(request);
    }
}


