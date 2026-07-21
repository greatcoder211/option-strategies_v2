package ownStrategy.logic.mapper.factory;
import org.springframework.stereotype.Component;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.entity.request.ButterflySpreadRequest;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.ButterflySpread;

import java.time.LocalDate;

@Component
public class ButterflySpreadFactory implements StrategyFactory <ButterflySpreadRequest> {
    @Override
    public OptionStrategy create(ButterflySpreadRequest request, double spotPrice) {
        if(request.getTradeDate() == null){
            request.setTradeDate(LocalDate.now());
        }
        return new ButterflySpread(
                request.getQuantity(),
                request.getPosition(),
                "https://www.tastylive.com/concepts-strategies/long-butterfly-spread",
                request.getOptionType(),
                request.getSpreadValue(),
                request.getTradeDate(),
                request.getExpiryDate(),
                spotPrice
        );
    }
    @Override
    public Class<ButterflySpreadRequest> getSupportedType() {
        return ButterflySpreadRequest.class;
    }
}
