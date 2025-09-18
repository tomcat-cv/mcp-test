package com.example.mcp.service;

import com.example.mcp.config.DownstreamProperties;
import com.example.mcp.model.DownstreamRequest;
import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Service
public class ForwardService {
	private final WebClient downstreamWebClient;
	private final DownstreamProperties props;

	public ForwardService(WebClient downstreamWebClient, DownstreamProperties props) {
		this.downstreamWebClient = downstreamWebClient;
		this.props = props;
	}

	public Mono<StandardQueryResponse> forward(StandardQueryRequest request) {
        System.out.println("[ForwardService] 收到标准请求: query='" + request.getQuery() + "', locale='" + request.getLocale() + "'");

        // 记录构造下游请求（即使当前不真实调用）
        DownstreamRequest downstreamRequest = new DownstreamRequest(
            request.getQuery(),
            request.getLocale(),
            request.getMetadata(),
            request.getTimestamp()
        );
        System.out.println("[ForwardService] 下游请求准备完毕, path='" + props.getPath() + "', apiKeyHeader='" + props.getApiKeyHeader() + "'");

        // 临时 MOCK：不进行真实下游调用，直接返回模拟结果
        return Mono.defer(() -> {
            System.out.println("[ForwardService] 使用 MOCK 响应替代真实下游调用");
            Map<String, Object> data = new HashMap<>();
            data.put("echoQuery", downstreamRequest.getQ());
            data.put("locale", downstreamRequest.getLang());
            data.put("metadata", downstreamRequest.getExtra());
            data.put("timestamp", downstreamRequest.getTs());
            data.put("downstream", "mock");

            StandardQueryResponse resp = new StandardQueryResponse();
            resp.setSuccess(true);
            resp.setMessage("MOCKED: 未调用真实下游服务");
            resp.setResult(data);

            System.out.println("[ForwardService] 返回 MOCK 响应: success=" + resp.isSuccess() + ", message='" + resp.getMessage() + "'");
            return Mono.just(resp);
        });
	}
}
