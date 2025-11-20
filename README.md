# MCP Forwarder (Java 21 + Spring Boot)

一个可独立部署的 MCP 转发/客户端套件，基于 Java 21 + Spring Boot，覆盖共享模型、MCP Server、两种风格的 MCP Client。

## 项目概览
- 采用多模块 Gradle 工程，所有模块共享 `common` 中的标准 DTO。
- `server` 提供符合 MCP 协议的 SSE 端点，可将请求转发到下游 HTTP 服务并流式返回。
- `client-spring-ai` 将 REST 查询交给 Spring AI + DeepSeek，再透传给 MCP Server，适合“对话式 + 自动工具”场景。
- `client-sdk` 直接使用 MCP SDK（无 Spring AI 依赖），支持 stdio/SSE、多连接、自动重连，更贴近原生 MCP 调试体验。

### 模块速览
| 模块 | 定位 | 入口类 | 默认端口 |
| --- | --- | --- | --- |
| `common` | 标准请求/响应模型 | — | — |
| `server` | MCP Server + `forward_query_stream` 工具 | `com.example.mcp.server.McpServerApplication` | 8080 |
| `client-spring-ai` | Spring AI + DeepSeek 的 REST Client | `com.example.mcp.client.McpClientApplication` | 8081 |
| `client-sdk` | 原生 MCP SDK Client（REST 控制面） | `com.example.mcp.client2.McpClient2Application` | 8082（可配） |

## 快速开始
1. **构建**
   ```bash
   ./gradlew clean build -x test
   ```
2. **启动 Server**（SSE 端点位于 `http://localhost:8080/sse`）
   ```bash
   java -jar server/build/libs/server-0.1.0.jar
   ```
3. **启动客户端（任选或同时）**
   ```bash
   # Spring AI 版本
   java -jar client-spring-ai/build/libs/client-spring-ai-0.1.0.jar

   # MCP SDK 版本
   java -jar client-sdk/build/libs/client-sdk-0.1.0.jar
   ```

## 模块详解
### common
- 定义 `StandardQueryRequest/Response`、`DownstreamRequest/Response` 等 DTO。
- 保证 Server 与不同客户端对 MCP 工具的参数结构保持一致。

### server
- 基于 Spring WebFlux + Spring AI MCP Server，暴露 `/sse` 流式端点。
- `ForwardTool#forwardStream` 工具会将标准请求转发到配置的下游 HTTP 服务并推送流式响应。
- 关键配置位于 `server/src/main/resources/application.properties`（见下文）。

### client-spring-ai（原 `client`）
- 通过 `ChatClient` + `ToolCallbackProvider` 将 REST 查询 prompt 交给 DeepSeek，再触发 MCP 工具。
- API：
  - `POST /api/mcp/query`：请求体 `{"query": "问题"}`，返回 `success/message/result`。
  - `GET /api/mcp/health`：健康检查。
- 运行：`./gradlew :client-spring-ai:bootRun` 或执行 JAR，默认端口 8081。
- 典型 curl：
  ```bash
  curl http://localhost:8081/api/mcp/health
  curl -X POST http://localhost:8081/api/mcp/query \
    -H "Content-Type: application/json" \
    -d '{"query":"你好"}'
  ```

### client-sdk（原 `client2`）
- 采用 `io.modelcontextprotocol.client` MCP SDK，支持多连接、自动重连、SSE 或 stdio 传输。
- 暴露的 REST 控制接口：
  - `GET /api/mcp/status`：查看各连接状态。
  - `POST /api/mcp/send`：直接发送 MCP 请求（例如 `tools/list`）。
  - `POST /api/mcp/reconnect`：触发重连。
- 运行：`./gradlew :client-sdk:bootRun` 或执行 JAR，默认端口可在 `server.port` 调整。
- 特性：初始化时自动调用 `initialize`、`tools/list`，并为每个工具创建 `MySyncMcpToolCallback`。

## 配置
### server
`server/src/main/resources/application.properties`
- `spring.ai.mcp.server.*`：MCP Server 元数据、SSE 路径（默认 `/sse`）。
- `downstream.base-url/path`：下游 HTTP 服务（默认 `http://localhost:9000/query`）。
- `downstream.api-key` / `downstream.api-key-header`：下游鉴权，可通过环境变量 `DOWNSTREAM_API_KEY` 覆盖。

### client-spring-ai
`client-spring-ai/src/main/resources/application.properties`
- `spring.ai.mcp.client.sse.connections.<name>.url` / `.sse-endpoint`：指向 MCP Server（默认 `http://localhost:8080` + `/sse`）。
- `spring.ai.deepseek.*`：DeepSeek API 配置，常用项：
  ```properties
  spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
  spring.ai.deepseek.base-url=https://api.deepseek.com/
  spring.ai.deepseek.chat.options.model=deepseek-chat
  ```
- `server.port`：REST 端口（默认 8081）。

> 建议通过 `.env` 或环境变量注入 `DEEPSEEK_API_KEY`，避免将密钥写入仓库。

### client-sdk
`client-sdk/src/main/resources/application.properties`
- 核心开关：`mcp.client.enabled=true`。
- 连接配置（SSE 示例）：
  ```properties
  mcp.client.connections.local.url=http://localhost:8080
  mcp.client.connections.local.sse-endpoint=/sse
  mcp.client.connections.local.timeout-seconds=50
  mcp.client.connections.local.client-name=spring-ai-mcp-client-local
  ```
- stdio 示例：
  ```properties
  mcp.client.type=stdio
  mcp.client.command=node /path/to/server.js
  ```
- 通用选项：
  ```properties
  mcp.client.type=sse
  mcp.client.serverUrl=http://localhost:3000/mcp
  mcp.client.timeout-ms=30000
  mcp.client.auto-reconnect=true
  mcp.client.reconnect-interval-ms=5000
  ```
- `server.port`：客户端控制面端口，默认 8082。

## 测试与排查
- **快速连通**：
  ```bash
  curl -v http://localhost:8080/sse -H "Accept: text/event-stream"
  ```
- **手动调用工具**：
  ```bash
  TIMESTAMP=$(date +%s000)
  curl -X POST http://localhost:8080/sse \
    -H "Content-Type: application/json" \
    -H "Accept: text/event-stream" \
    --no-buffer --max-time 15 \
    -d "{
      \"jsonrpc\":\"2.0\",
      \"id\":1,
      \"method\":\"tools/call\",
      \"params\":{
        \"name\":\"forward_query\",
        \"arguments\":{
          \"seqNo\":\"test-${TIMESTAMP}\",
          \"systemCode\":\"TEST_SYSTEM\",
          \"timestamp\":${TIMESTAMP}
        }
      }
    }"
  ```
- **client-sdk 发送自定义请求**：
  ```bash
  curl -X POST http://localhost:8082/api/mcp/send \
    -H "Content-Type: application/json" \
    -d '{
      "method": "tools/list",
      "params": {}
    }'
  ```
- 常见错误与定位：`Session ID missing` -> 检查 initialize；`Connection refused` -> 确认 Server/SSE URL；`401` -> 下游 API Key 设置。

## 数据模型速览
```text
StandardQueryRequest {
  seqNo, systemCode, timestamp
}

StandardQueryResponse {
  success, message, result({downstreamRequestReal, progress})
}

DownstreamRequest { seqNo, systemCode }
DownstreamResponse { status, message, data }
```

## 参考命令
- 运行测试：`./gradlew :<module>:test`
- 构建单模块 Jar：`./gradlew :client-spring-ai:bootJar`
- 清理：`./gradlew clean`

> 如需扩展更多 MCP 客户端，可复用 `common` 中的模型，并在根 `settings.gradle` 新增模块定义。
