package ownStrategy.logic.mapper.factory;

import ownStrategy.dto.strategyPanel.RatioSpreadRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.asymmetrical.RatioSpread;

public class RatioSpreadFactory implements StrategyFactory<RatioSpreadRequest> {

    @Override
    public OptionStrategy create(RatioSpreadRequest request) {
        return new RatioSpread(
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
    public Class<RatioSpreadRequest> getSupportedType() {
        return RatioSpreadRequest.class;
    }
}
