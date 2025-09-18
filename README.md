# MCP Forwarder (Java 21 + Spring Boot, STDIO only)

一个最小可用的 MCP 转发服务：接收标准化输入，将请求转发至下游 HTTP 接口，并将结果映射为标准响应返回。同时提供基于 STDIO 的 MCP 服务器接口（JSON-RPC 2.0）。

## 运行环境
- Java 21
- Gradle 8+

## 构建
```bash
./gradlew clean bootJar
```
生成的 Jar：
- MCP 模式（唯一产物）：`build/libs/mcp-forwarder-0.1.0.jar`

## 运行（MCP / STDIO）
```bash
java -jar build/libs/mcp-forwarder-0.1.0.jar
```

> 入口类：`com.example.mcp.mcp.McpServerApplication`（非 Web 模式），通过 STDIN/STDOUT 使用 JSON-RPC 与宿主通信。

### JSON-RPC 示例
- initialize
```json
{"jsonrpc":"2.0","id":"1","method":"initialize","params":{}}
```
- 列出工具
```json
{"jsonrpc":"2.0","id":"2","method":"tools/list","params":{}}
```
- 调用工具（forward_query）
```json
{
  "jsonrpc": "2.0",
  "id": "3",
  "method": "tools/call",
  "params": {
    "name": "forward_query",
    "arguments": {
      "query": "天气怎么样",
      "locale": "zh-CN",
      "metadata": {"sessionId": "abc-123"},
      "timestamp": 1699999999000
    }
  }
}
```

## 配置
在 `src/main/resources/application.yml` 中配置，或通过环境变量覆盖：
- `downstream.base-url`：下游服务基础地址（默认 `http://localhost:9000`）
- `downstream.path`：下游请求路径（默认 `/query`）
- `downstream.api-key`：下游鉴权密钥（可选，通过环境变量 `DOWNSTREAM_API_KEY` 注入）
- `downstream.api-key-header`：密钥请求头名（默认 `Authorization`）

## 下游接口期望
- 请求映射：
  - `q` <- `query`
  - `lang` <- `locale`
  - `extra` <- `metadata`
  - `ts` <- `timestamp`
- 响应示例：
```json
{"status":"ok","message":"ok","data":{"answer":"晴"}}
```

## 说明
- MCP STDIO 服务器支持 `initialize`、`tools/list`、`tools/call`，其中 `tools/call` 调用现有 `ForwardService` 完成转发。
- 字段是占位设计，可后续按标准化协议与下游接口再行调整。
