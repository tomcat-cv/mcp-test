# MCP Client2 模块

这是一个使用Spring Boot和MCP SDK（不包含Spring AI）构建的MCP客户端模块。

## 功能特性

- 使用原生的MCP SDK而不是Spring AI
- 支持stdio和SSE两种连接方式
- 自动重连机制
- RESTful API接口
- 配置化管理

## 依赖

- Spring Boot 3.3.3
- MCP SDK 0.5.0
- Lombok
- Spring WebFlux

## 配置

在 `application.properties` 中配置MCP客户端：

### stdio模式
```properties
mcp.client.type=stdio
mcp.client.command=node /path/to/your/mcp-server.js
```

### SSE模式
```properties
mcp.client.type=sse
mcp.client.serverUrl=http://localhost:3000/mcp
```

### 其他配置
```properties
mcp.client.timeout-ms=30000
mcp.client.auto-reconnect=true
mcp.client.reconnect-interval-ms=5000
```

## API接口

### 获取连接状态
```
GET /api/mcp/status
```

### 发送MCP请求
```
POST /api/mcp/send
Content-Type: application/json

{
  "method": "tools/list",
  "params": {}
}
```

### 重新连接
```
POST /api/mcp/reconnect
```

## 运行

```bash
./gradlew :client2:bootRun
```

或者构建JAR文件：
```bash
./gradlew :client2:bootJar
java -jar client2/build/libs/client2.jar
```

## 注意事项

1. 确保MCP服务器正在运行并且可以通过配置的方式访问
2. 对于stdio模式，确保命令路径正确且可执行
3. 对于SSE模式，确保服务器URL可访问
4. 查看日志以获取详细的连接和错误信息
