package com.example.mcp.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.mcp.model.DownstreamRequest;
import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;

import reactor.core.publisher.Flux;

@Service
public class ForwardService {

	public Flux<StandardQueryResponse> forwardStream(StandardQueryRequest request) {
		System.out.println("[ForwardService] 开始流式处理: request='" + request + "'");

		DownstreamRequest downstreamRequest = new DownstreamRequest();
		downstreamRequest.setSeqNo(request.getSeqNo());
		downstreamRequest.setSystemCode(request.getSystemCode());

		Map<String, Object> baseData = new HashMap<>();
		baseData.put("downstreamRequestReal", downstreamRequest);

		// 使用定时器模拟分段事件，以便通过 SSE 推送
		return Flux.interval(Duration.ZERO, Duration.ofMillis(400))
				.take(5)
				.map(idx -> {
					// mock返回测试用
					StandardQueryResponse resp = new StandardQueryResponse();
					resp.setSuccess(true);
					resp.setResult(new HashMap<>(baseData));
					if (idx < 4) {
						resp.setMessage("STREAMING: 处理中 step " + (idx + 1));
						resp.getResult().put("progress", (idx + 1) * 20);
					} else {
						resp.setMessage("STREAMING: 完成");
						resp.getResult().put("progress", 100);
					}
					System.out.println("[ForwardService] 流事件 " + idx + ": message='" + resp.getMessage() + "'");
					return resp;
				});
	}
}
