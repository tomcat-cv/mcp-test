package com.example.mcp.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {
	@Bean
	public WebClient downstreamWebClient(DownstreamProperties props,
	                                     @Value("${spring.application.name:mcp-forwarder}") String appName) {
		// 配置超时
		HttpClient httpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 5秒连接超时
				.responseTimeout(Duration.ofSeconds(30)) // 30秒读取超时
				.doOnConnected(conn -> conn.addHandlerLast(
						new ReadTimeoutHandler(30, TimeUnit.SECONDS)
				));
		
		return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(props.getBaseUrl() != null ? props.getBaseUrl() : "")
				.defaultHeader("User-Agent", appName + "/1.0")
				.build();
	}
}
