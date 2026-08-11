package ownStrategy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
@ConfigurationProperties(prefix = "integration.alphavantage")
public record AlphaVantageProperties(String apiKey, Duration connectTimeOut) {
}
