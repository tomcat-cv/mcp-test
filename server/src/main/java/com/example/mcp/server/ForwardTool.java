package com.example.mcp.server;

import org.springframework.ai.tool.annotation.Tool;

import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;
import com.example.mcp.service.ForwardService;

import reactor.core.publisher.Flux;

public class ForwardTool {
    private final ForwardService forwardService;

    public ForwardTool(ForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @Tool(name = "forward_query_stream", description = "以SSE流式返回标准化转发的过程事件")
    public Flux<StandardQueryResponse> forwardStream(StandardQueryRequest request) {
        System.out.println("[ForwardTool] 收到流式请求: query='" + request.getQuery() + "', locale='" + request.getLocale() + "'");
        return forwardService.forwardStream(request)
            .doOnError(ex -> {
                System.err.println("[ForwardTool] 流式处理失败: " + ex.getMessage());
                ex.printStackTrace();
            });
    }
}


