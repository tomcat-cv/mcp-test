package com.example.mcp.server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;
import com.example.mcp.service.ForwardService;

import reactor.core.publisher.Mono;


@Service
public class ForwardTool {
    private final ForwardService forwardService;

    public ForwardTool(ForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @Tool(name = "forward_query", description = "将标准化输入转发到下游并返回处理结果")
    public Mono<StandardQueryResponse> forwardQuery(StandardQueryRequest request) {
        System.out.println("[ForwardTool] 收到请求: request='" + request + "'");
        try {
            return forwardService.forwardQuery(request);
        } catch (Exception ex) {
            System.err.println("[ForwardTool] 处理失败: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }
}