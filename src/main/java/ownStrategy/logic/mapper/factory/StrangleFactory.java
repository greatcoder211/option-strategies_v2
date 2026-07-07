package ownStrategy.logic.mapper.factory;

import ownStrategy.dto.strategyPanel.StrangleRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.asymmetrical.RatioSpread;
import ownStrategy.model.strategy.templates.vertical.Strangle;

public class StrangleFactory implements StrategyFactory<StrangleRequest> {

    @Override
    public OptionStrategy create(StrangleRequest request) {
        return new Strangle(
                request.getQuantity(),
                request.getPosition(),
                request.getSpreadValue(),
                request.getTradeDate(),
                request.getExpiryDate(),
                request.getSpotPrice()
        );
    }

    @Override
    public Class<StrangleRequest> getSupportedType() {
        return StrangleRequest.class;
    }
}
