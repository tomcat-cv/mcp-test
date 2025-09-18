package com.example.mcp.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClientCallsServerIT {

    @LocalServerPort
    int clientPort;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.mcp.client.sse.connections.local.url", () -> "http://localhost:8080");
        registry.add("spring.ai.mcp.client.sse.connections.local.sse-endpoint", () -> "/sse");
    }

    @Test
    void clientHealthAndServerSseReachable() {
        WebClient http = WebClient.builder()
            .baseUrl("http://localhost:" + clientPort)
            .build();

        var health = http.get().uri("/client/mcp/health")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .block(Duration.ofSeconds(5));

        assertThat(health).isNotNull();
        assertThat(health).contains("status");

        // 直连 server 的 SSE 端点检查：返回 200 且为 text/event-stream，并可读取至少一条事件
        Integer code = HttpClient.create()
            .baseUrl("http://localhost:8080")
            .headers(h -> h.add("Accept", MediaType.TEXT_EVENT_STREAM_VALUE))
            .get()
            .uri("/sse")
            .responseSingle((res, buf) -> Mono.just(res.status().code()))
            .block(Duration.ofSeconds(5));
        assertThat(code).isBetween(200, 204);

        String contentType = HttpClient.create()
            .baseUrl("http://localhost:8080")
            .headers(h -> h.add("Accept", MediaType.TEXT_EVENT_STREAM_VALUE))
            .get()
            .uri("/sse")
            .responseSingle((res, buf) -> Mono.justOrEmpty(res.responseHeaders().get("Content-Type")))
            .block(Duration.ofSeconds(5));
        assertThat(contentType).isNotNull();
        assertThat(contentType).contains("text/event-stream");

        String firstEvent = HttpClient.create()
            .baseUrl("http://localhost:8080")
            .headers(h -> h.add("Accept", MediaType.TEXT_EVENT_STREAM_VALUE))
            .get()
            .uri("/sse")
            .response((res, content) -> {
                if (res.status().code() < 200 || res.status().code() >= 300) {
                    return Mono.error(new IllegalStateException("SSE status=" + res.status().code()));
                }
                return content.asString().filter(s -> !s.isBlank()).take(1).singleOrEmpty();
            })
            .blockFirst(Duration.ofSeconds(10));
        assertThat(firstEvent).isNotNull();
    }
}


