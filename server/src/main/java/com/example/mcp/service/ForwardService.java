package com.example.mcp.service;

import com.example.mcp.config.DownstreamProperties;
import com.example.mcp.model.DownstreamRequest;
import com.example.mcp.model.DownstreamResponse;
import com.example.mcp.model.StandardQueryRequest;
import com.example.mcp.model.StandardQueryResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ForwardService {
	private final WebClient downstreamWebClient;
	private final DownstreamProperties props;

	public ForwardService(WebClient downstreamWebClient, DownstreamProperties props) {
		this.downstreamWebClient = downstreamWebClient;
		this.props = props;
	}

	public Mono<StandardQueryResponse> forward(StandardQueryRequest request) {
		DownstreamRequest downstreamRequest = new DownstreamRequest(
			request.getQuery(),
			request.getLocale(),
			request.getMetadata(),
			request.getTimestamp()
		);

		return downstreamWebClient
			.post()
			.uri(props.getPath())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.headers(headers -> {
				if (props.getApiKey() != null && !props.getApiKey().isEmpty()) {
					headers.set(props.getApiKeyHeader(), props.getApiKey());
				}
			})
			.body(BodyInserters.fromValue(downstreamRequest))
			.retrieve()
			.bodyToMono(DownstreamResponse.class)
			.map(ds -> {
				StandardQueryResponse resp = new StandardQueryResponse();
				resp.setSuccess("ok".equalsIgnoreCase(ds.getStatus()));
				resp.setMessage(ds.getMessage());
				resp.setResult(ds.getData());
				return resp;
			});
	}
}
