package ownStrategy.config;

import org.springframework.context.annotation.Configuration;
import ownStrategy.logic.finance.ChartGenerator;

@Configuration
public class FinanceConfig {
    public ChartGenerator chart(){
        return new ChartGenerator();
    }
}
