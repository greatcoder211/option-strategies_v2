package ownStrategy.logic.mapper.factory;

import ownStrategy.dto.request.VerticalSpreadRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.VerticalSpread;

public class VerticalSpreadFactory implements StrategyFactory <VerticalSpreadRequest>{
    @Override
    public OptionStrategy create(VerticalSpreadRequest request) {
        return new VerticalSpread(
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
    public Class<VerticalSpreadRequest> getSupportedType() {
        return VerticalSpreadRequest.class;
    }
}
