package ownStrategy.config;

import org.springframework.context.annotation.Configuration;
import ownStrategy.logic.finance.ChartGenerator;
import ownStrategy.logic.finance.OptionCalculator;

@Configuration
public class FinanceConfig {
    public OptionCalculator optionCalculator(){
        return new OptionCalculator();
    }
    public ChartGenerator chart(){
        return new ChartGenerator();
    }
}
