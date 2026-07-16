package ownStrategy.logic.mapper.factory;

import ownStrategy.dto.request.IronButterflyRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.IronButterfly;

public class IronButterflyFactory implements StrategyFactory<IronButterflyRequest> {
    @Override
    public OptionStrategy create(IronButterflyRequest request) {
        return new IronButterfly(
                request.getQuantity(),
                request.getPosition(),
                request.getSpreadValue(),
                request.getTradeDate(),
                request.getExpiryDate(),
                request.getSpotPrice()
        );
    }
    @Override
    public Class<IronButterflyRequest> getSupportedType() {
        return IronButterflyRequest.class;
    }
}
