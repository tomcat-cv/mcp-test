package com.example.mcp.mcp;

import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;
import com.example.mcp.service.ForwardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 极简 STDIO JSON-RPC 2.0 服务器，提供 MCP 所需的基础方法：
 * - initialize
 * - tools/list
 * - tools/call -> 调用 ForwardService
 */
public class StdioJsonRpcServer {
	private final ObjectMapper mapper = new ObjectMapper();
	private final ForwardService forwardService;

	public StdioJsonRpcServer(ForwardService forwardService) {
		this.forwardService = forwardService;
	}

	public void run() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
			 PrintWriter writer = new PrintWriter(System.out, true, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) continue;
				try {
					JsonNode node = mapper.readTree(line);
					JsonNode idNode = node.get("id");
					String id = idNode != null && !idNode.isNull() ? idNode.asText() : UUID.randomUUID().toString();
					String method = node.path("method").asText("");
					JsonNode params = node.path("params");

					Object result = handle(method, params);
					writeResponse(writer, id, result);
				} catch (Exception ex) {
					writeError(writer, UUID.randomUUID().toString(), -32603, ex.getMessage());
				}
			}
		}
	}

	private Object handle(String method, JsonNode params) throws Exception {
		switch (method) {
			case "initialize":
				Map<String, Object> init = new HashMap<>();
				init.put("protocolVersion", "2024-11-05");
				init.put("capabilities", Map.of("tools", true));
				return init;
			case "tools/list":
				Map<String, Object> tool = new HashMap<>();
				tool.put("name", "forward_query");
				tool.put("description", "将标准化输入转发到下游HTTP服务");
				tool.put("inputSchema", Map.of(
					"type", "object",
					"properties", Map.of(
						"query", Map.of("type", "string"),
						"locale", Map.of("type", "string"),
						"metadata", Map.of("type", "object"),
						"timestamp", Map.of("type", "number")
					),
					"required", new String[]{"query","timestamp"}
				));
				return Map.of("tools", new Object[]{ tool });
			case "tools/call":
				String name = params.path("name").asText("");
				if (!"forward_query".equals(name)) {
					return Map.of("error", "Unknown tool: " + name);
				}
				JsonNode args = params.path("arguments");
				StandardQueryRequest req = mapper.treeToValue(args, StandardQueryRequest.class);
				StandardQueryResponse resp = forwardService.forward(req).onErrorResume(e -> Mono.fromCallable(() -> {
					StandardQueryResponse r = new StandardQueryResponse();
					r.setSuccess(false);
					r.setMessage(e.getMessage());
					return r;
				})).block();
				return Map.of(
					"content", new Object[]{ Map.of(
						"type", "text",
						"text", toSafeJson(resp)
					)}
				);
			default:
				return Map.of("error", "Unknown method: " + method);
		}
	}

	private void writeResponse(PrintWriter writer, String id, Object result) throws JsonProcessingException {
		Map<String, Object> envelope = new HashMap<>();
		envelope.put("jsonrpc", "2.0");
		envelope.put("id", id);
		envelope.put("result", result);
		writer.println(mapper.writeValueAsString(envelope));
	}

	private void writeError(PrintWriter writer, String id, int code, String message) throws JsonProcessingException {
		Map<String, Object> envelope = new HashMap<>();
		envelope.put("jsonrpc", "2.0");
		envelope.put("id", id);
		envelope.put("error", Map.of("code", code, "message", message));
		writer.println(mapper.writeValueAsString(envelope));
	}

	private String toSafeJson(Object o) {
		try {
			return mapper.writeValueAsString(o);
		} catch (Exception e) {
			return "{" + "\"error\"" + ":\"serialization failed\"}";
		}
	}
}
