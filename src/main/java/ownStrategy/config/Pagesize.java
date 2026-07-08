package ownStrategy.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties("strategy.pagination")
public class Pagesize {
    private int pageSize = 10;
}
