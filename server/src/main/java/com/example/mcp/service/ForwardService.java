package com.example.mcp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.mcp.model.DownstreamRequest;
import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;


@Service
public class ForwardService {

	public StandardQueryResponse forwardQuery(StandardQueryRequest request) {
		System.out.println("[ForwardService] 开始处理: request='" + request + "'");

		DownstreamRequest downstreamRequest = new DownstreamRequest();
		downstreamRequest.setSeqNo(request.getSeqNo());
		downstreamRequest.setSystemCode(request.getSystemCode());

		Map<String, Object> baseData = new HashMap<>();
		baseData.put("downstreamRequestReal", downstreamRequest);

		// 模拟处理延迟
		try {
			Thread.sleep(1000); // 模拟处理时间
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// 返回处理结果
		StandardQueryResponse resp = new StandardQueryResponse();
		resp.setSuccess(true);
		resp.setResult(new HashMap<>(baseData));
		resp.setMessage("处理完成");
		resp.getResult().put("progress", 100);
		
		System.out.println("[ForwardService] 处理完成: message='" + resp.getMessage() + "'");
		return resp;
	}
}
