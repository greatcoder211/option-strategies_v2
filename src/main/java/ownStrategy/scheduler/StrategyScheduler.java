package ownStrategy.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ownStrategy.service.strategy.StrategyService;

@Component
public class StrategyScheduler {
    private final StrategyService strategyService;
    public StrategyScheduler(StrategyService strategyService) {
        this.strategyService = strategyService;
    }
    @Scheduled
    public void closeExpired() {
        strategyService.closeExpiredStrategies();
    }
}
