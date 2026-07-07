package ownStrategy.logic.mapper.factory;
import org.springframework.stereotype.Component;
import ownStrategy.dto.strategyPanel.ButterflySpreadRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.ButterflySpread;
@Component
public class ButterflySpreadFactory implements StrategyFactory <ButterflySpreadRequest> {
    @Override
    public OptionStrategy create(ButterflySpreadRequest request) {
        return new ButterflySpread(
                request.getQuantity(),
                request.getPosition(),
                request.getOptionType(),
                request.getSpreadValue(),
                request.getTradeDate(),
                request.getExpiryDate(),
                request.getSpotPrice()
        );
    }
    @Override
    public Class<ButterflySpreadRequest> getSupportedType() {
        return ButterflySpreadRequest.class;
    }
}
