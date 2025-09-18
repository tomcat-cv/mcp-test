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
        System.out.println("[ForwardTool] 收到请求: query='" + request.getQuery() + "', locale='" + request.getLocale() + "'");
        return forwardService.forward(request)
            .doOnSuccess(resp -> {
                if (resp != null) {
                    System.out.println("[ForwardTool] 返回响应: success=" + resp.isSuccess() + ", message='" + resp.getMessage() + "'");
                } else {
                    System.err.println("[ForwardTool] 返回空响应");
                }
            })
            .doOnError(ex -> {
                System.err.println("[ForwardTool] 处理失败: " + ex.getMessage());
                ex.printStackTrace();
            });
    }
}


