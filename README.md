# MCP Forwarder (Java 21 + Spring Boot, Multi-Module)

一个可独立部署的 MCP 转发系统，支持流式处理和 AI 集成，已拆分为三个模块：
- `common`：共享模型（DTO），包含标准化的请求/响应结构。
- `server`：MCP Server（基于 Spring WebFlux + Spring AI MCP Server WebFlux，提供流式 SSE 端点 `/sse`）。
- `client`：MCP Client（基于 Spring AI MCP Client WebFlux，集成 DeepSeek API，提供 REST API 接口）。

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
- 暴露 SSE 端点：`/sse`（流式 MCP 工具调用）
- 端口：8080（默认）

### 启动 Client（作为 MCP Client 连接 Server）
```bash
java -jar client/build/libs/client-0.1.0.jar
```
- 入口类：`com.example.mcp.client.McpClientApplication`
- 端口：8081（默认）
- REST API 端点：
  - `POST /api/mcp/query` - 查询接口（集成 DeepSeek AI）
  - `GET /api/mcp/health` - 健康检查

> 说明：本项目采用 Spring AI MCP 的 WebFlux 形态，通过 HTTP SSE 与客户端交互，支持流式处理。Client 集成了 DeepSeek API 提供 AI 能力。

## 配置

### Server 配置（`server/src/main/resources/application.properties`）
- `spring.ai.mcp.server.*`：MCP Server 基本信息与 SSE 端点（默认 `/sse`）。
- `downstream.*`：下游 HTTP 服务配置（支持环境变量覆盖）。
  - `downstream.base-url`：下游基础地址（默认 `http://localhost:9000`）。
  - `downstream.path`：请求路径（默认 `/query`）。
  - `downstream.api-key`：下游鉴权密钥（可选，支持 `DOWNSTREAM_API_KEY`）。
  - `downstream.api-key-header`：密钥请求头名称（默认 `Authorization`）。

### Client 配置（`client/src/main/resources/application.properties`）
- `spring.ai.mcp.client.*`：MCP Client 配置。
- `spring.ai.deepseek.*`：DeepSeek AI API 配置。
  - `spring.ai.deepseek.api-key`：DeepSeek API 密钥（必需，支持环境变量 `${DEEPSEEK_API_KEY}`）。
  - `spring.ai.deepseek.base-url`：API 基础地址（默认 `https://api.deepseek.com/`）。
  - `spring.ai.deepseek.chat.options.model`：使用的模型（默认 `deepseek-chat`）。
- SSE 连接配置：
  - `spring.ai.mcp.client.sse.connections.local.url=http://localhost:8080`
  - `spring.ai.mcp.client.sse.connections.local.sse-endpoint=/sse`
- `server.port`：客户端服务端口（默认 8081）。

## API 接口说明

### Client REST API

#### 查询接口
- **端点**：`POST /api/mcp/query`
- **请求体**：
```json
{
  "query": "你的问题"
}
```
- **响应体**：
```json
{
  "success": true,
  "message": "查询成功",
  "result": "AI 回复内容"
}
```

#### 健康检查
- **端点**：`GET /api/mcp/health`
- **响应**：`"MCP Client is running"`

### Server MCP 工具

#### 流式查询工具
- **工具名**：`forward_query_stream`
- **描述**：将标准化输入转发到下游并返回流式事件
- **输入**：`StandardQueryRequest`
- **输出**：`Flux<StandardQueryResponse>`（流式响应）

### 数据模型

#### StandardQueryRequest
```java
{
  "seqNo": "序列号",
  "systemCode": "系统代码", 
  "timestamp": 1234567890
}
```

#### StandardQueryResponse
```java
{
  "success": true,
  "message": "处理状态信息",
  "result": {
    "downstreamRequestReal": {...},
    "progress": 100
  }
}
```

#### DownstreamRequest
```java
{
  "seqNo": "序列号",
  "systemCode": "系统代码"
}
```

#### DownstreamResponse
```java
{
  "status": "ok",
  "message": "处理结果",
  "data": {...}
}
```

## 模块说明
- `common`：
  - 数据模型：`StandardQueryRequest`、`StandardQueryResponse`、`DownstreamRequest`、`DownstreamResponse`。
  - 提供标准化的请求/响应结构，支持流式处理。
- `server`：
  - 入口：`com.example.mcp.server.McpServerApplication`
  - 工具：`ForwardTool#forwardStream`（工具名 `forward_query_stream`），支持流式响应。
  - 服务：`ForwardService` 提供流式处理逻辑，模拟分段事件推送。
  - 配置：`DownstreamProperties`、`WebClientConfig`。
- `client`：
  - 入口：`com.example.mcp.client.McpClientApplication`
  - 控制器：`McpController` 提供 REST API 接口。
  - 集成：DeepSeek AI API，提供智能问答能力。

## 使用示例

### 1. 启动服务
```bash
# 启动 Server（端口 8080）
java -jar server/build/libs/server-0.1.0.jar

# 启动 Client（端口 8081）
java -jar client/build/libs/client-0.1.0.jar
```

### 2. 测试 Client API
```bash
# 健康检查
curl http://localhost:8081/api/mcp/health

# 查询接口
curl -X POST http://localhost:8081/api/mcp/query \
  -H "Content-Type: application/json" \
  -d '{"query": "你好，请介绍一下自己"}'
```

### 3. 测试 Server 流式工具
通过 MCP 客户端连接到 `http://localhost:8080/sse`，调用 `forward_query_stream` 工具：
```json
{
  "seqNo": "test-001",
  "systemCode": "TEST_SYSTEM",
  "timestamp": 1703123456789
}
```

### 4. 配置 API 密钥

#### 方法一：使用环境变量（推荐）
1. 复制环境变量模板文件：
```bash
cp .env.example .env
```

2. 编辑 `.env` 文件，填入您的实际API密钥：
```bash
# DeepSeek API配置
DEEPSEEK_API_KEY=sk-your-actual-deepseek-api-key-here

# 下游服务API密钥（可选）
DOWNSTREAM_API_KEY=your-downstream-api-key-here
```

3. 启动应用时加载环境变量：
```bash
# 启动 Server
java -jar server/build/libs/server-0.1.0.jar

# 启动 Client
java -jar client/build/libs/client-0.1.0.jar
```

#### 方法二：直接设置环境变量
```bash
# 设置 DeepSeek API 密钥
export DEEPSEEK_API_KEY=sk-your-actual-deepseek-api-key-here

# 设置下游服务API密钥（可选）
export DOWNSTREAM_API_KEY=your-downstream-api-key-here

# 启动应用
java -jar client/build/libs/client-0.1.0.jar
```

#### 方法三：在配置文件中直接配置（不推荐）
在 `client/src/main/resources/application.properties` 中配置：
```properties
spring.ai.deepseek.api-key=your-deepseek-api-key
spring.ai.deepseek.chat.options.model=deepseek-chat
```

> **安全提示**：
> - 强烈推荐使用方法一或方法二，避免将API密钥直接写入代码仓库
> - `.env` 文件已被添加到 `.gitignore` 中，不会被提交到Git仓库
> - 请妥善保管您的API密钥，不要分享给他人
