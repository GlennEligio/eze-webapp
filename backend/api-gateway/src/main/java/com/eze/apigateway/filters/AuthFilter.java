package com.eze.apigateway.filters;

import com.eze.apigateway.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey("Authorization")){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No Authorization header present");
            }

            String authString = Objects.requireNonNull(exchange.getRequest().getHeaders().get("Authorization")).get(0);
            String[] parts = authString.split(" ");

            if(!Objects.equals(parts[0], "Bearer") && parts[1].length() == 0){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect Authentication Type");
            }

            return webClientBuilder.build()
                    .get().uri(userServiceUrl + "/validate/" + parts[1])
                    .retrieve().bodyToMono(UserDto.class)
                    .map(userDto -> {
                        exchange.getRequest().mutate().headers(httpHeaders -> {
                            httpHeaders.add("X-auth-role", userDto.getRole());
                            httpHeaders.add("X-auth-username", userDto.getUsername());
                        });
                        return exchange;
                    }).flatMap(chain::filter);
        });
    }

    public static class Config {
        // empty
    }
}
