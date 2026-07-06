package ownStrategy.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties("strategy.pricing.context")
public class DefaultPricingContext {
    private double riskFreeRate;
    private double volatility;
}