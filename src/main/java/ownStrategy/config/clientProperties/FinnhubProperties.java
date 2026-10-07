package ownStrategy.config.clientProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
@ConfigurationProperties(prefix = "integration.finnhub")
public record FinnhubProperties (String apiToken, Duration connectTimeOut) { }
