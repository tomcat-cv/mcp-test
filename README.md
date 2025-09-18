# MCP Forwarder (Java 21 + Spring Boot, Multi-Module)

一个可独立部署的 MCP 转发系统，已拆分为三个模块：
- `common`：共享模型（DTO）。
- `server`：MCP Server（基于 Spring WebFlux + Spring AI MCP Server WebFlux，SSE `/sse`）。
- `client`：MCP Client（基于 Spring AI MCP Client WebFlux，可通过 SSE 连接 `server`）。

## 运行环境
- Java 21
- Gradle 8+

## 构建
在工程根目录执行：
```bash
./gradlew clean build -x test
```
生成的可执行 Jar：
- Server：`server/build/libs/server-0.1.0.jar`
- Client：`client/build/libs/client-0.1.0.jar`

## 运行
分别在不同进程或机器上启动：

### 启动 Server（提供 MCP Server via SSE）
```bash
java -jar server/build/libs/server-0.1.0.jar
```
- 入口类：`com.example.mcp.server.McpServerApplication`
- 暴露 SSE 端点：`/sse`（见 `server/src/main/resources/application.yml`）

### 启动 Client（作为 MCP Client 连接 Server）
```bash
java -jar client/build/libs/client-0.1.0.jar
```
- 入口类：`com.example.mcp.client.McpClientApplication`
- 健康检查：`GET /client/mcp/health`

> 说明：本项目采用 Spring AI MCP 的 WebFlux 形态，通过 HTTP SSE 与客户端交互，不再是 STDIO 形态。

## 配置

### Server 配置（`server/src/main/resources/application.yml`）
- `spring.ai.mcp.server.*`：MCP Server 基本信息与 SSE 端点（默认 `/sse`）。
- `downstream.*`：下游 HTTP 服务配置（支持环境变量覆盖）。
  - `downstream.base-url`：下游基础地址（默认 `http://localhost:9000`）。
  - `downstream.path`：请求路径（默认 `/query`）。
  - `downstream.api-key`：下游鉴权密钥（可选，支持 `DOWNSTREAM_API_KEY`）。
  - `downstream.api-key-header`：密钥请求头名称（默认 `Authorization`）。

### Client 配置（`client/src/main/resources/application.yml` 或 `application.properties`）
- `spring.ai.mcp.client.*`：MCP Client 配置。
- 示例（连接本地 Server）：
  - `spring.ai.mcp.client.sse.connections.local.url=http://localhost:8080`
  - `spring.ai.mcp.client.sse.connections.local.sse-endpoint=/sse`

## 下游接口期望（Server -> 下游）
- 请求映射：
  - `q` <- `query`
  - `lang` <- `locale`
  - `extra` <- `metadata`
  - `ts` <- `timestamp`
- 响应示例：
```json
{"status":"ok","message":"ok","data":{"answer":"晴"}}
```

## 模块说明
- `common`：
  - 数据模型：`StandardQueryRequest`、`StandardQueryResponse`、`DownstreamRequest`、`DownstreamResponse`。
- `server`：
  - 入口：`com.example.mcp.server.McpServerApplication`
  - 工具：`ForwardTool#forward`（工具名 `forward_query`），调用 `ForwardService` 转发到下游。
  - 配置：`DownstreamProperties`、`WebClientConfig`。
- `client`：
  - 入口：`com.example.mcp.client.McpClientApplication`
  - 探针：`McpClientProbeController`（`/client/mcp/health`）。

> 字段与协议映射可按需调整，以对齐你的下游接口与业务语义。
