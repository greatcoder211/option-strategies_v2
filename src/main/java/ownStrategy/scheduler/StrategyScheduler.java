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
//    @PatchMapping metody schedulera działają w tle, nie obsługują żądań http
//    @Scheduled(cron = "0 0 12 * * *") codziennie o dwunastej: (sekunda minuta godzina dzień miesiąca miesiąc dzień tygodnia)
    @Scheduled(cron = "@daily")
    public void closeExpired() {
            strategyService.closeExpiredStrategies();
        }
}
