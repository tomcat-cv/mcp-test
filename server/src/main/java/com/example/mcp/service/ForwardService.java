package com.example.mcp.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.mcp.model.DownstreamRequest;
import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;

import reactor.core.publisher.Mono;


@Service
public class ForwardService {

	public Mono<StandardQueryResponse> forwardQuery(StandardQueryRequest request) {
		System.out.println("[ForwardService] 开始处理: request='" + request + "'");

		DownstreamRequest downstreamRequest = new DownstreamRequest();
		downstreamRequest.setSeqNo(request.getSeqNo());
		downstreamRequest.setSystemCode(request.getSystemCode());

		Map<String, Object> baseData = new HashMap<>();
		baseData.put("downstreamRequestReal", downstreamRequest);

		// 使用响应式延迟替代 Thread.sleep
		return Mono.delay(Duration.ofSeconds(1))
				.map(tick -> {
					// 返回处理结果
					StandardQueryResponse resp = new StandardQueryResponse();
					resp.setSuccess(true);
					resp.setResult(new HashMap<>(baseData));
					resp.setMessage("处理完成");
					resp.getResult().put("progress", 100);
					
					System.out.println("[ForwardService] 处理完成: message='" + resp.getMessage() + "'");
					return resp;
				});
	}
}
