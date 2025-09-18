package com.example.mcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
	@Bean
	public WebClient downstreamWebClient(DownstreamProperties props,
	                                    @Value("${spring.application.name:mcp-forwarder}") String appName) {
		HttpClient httpClient = HttpClient.create();

		ExchangeStrategies strategies = ExchangeStrategies.builder()
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
			.build();

		WebClient.Builder builder = WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.exchangeStrategies(strategies)
			.defaultHeader("User-Agent", appName + "/1.0")
			.baseUrl(props.getBaseUrl() != null ? props.getBaseUrl() : "http://localhost:9000");

		return builder.build();
	}
}
