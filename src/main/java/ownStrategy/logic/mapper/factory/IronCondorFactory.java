package ownStrategy.logic.mapper.factory;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.IronCondorRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.IronCondor;
@Component
public class IronCondorFactory implements StrategyFactory <IronCondorRequest>{
    @Override
    public OptionStrategy create(IronCondorRequest request) {
        return new IronCondor(
                request.getQuantity(),
                request.getPosition(),
                request.getSpreadValues(),
                request.getTradeDate(),
                request.getExpiryDate(),
                request.getSpotPrice()
        );
    }
    @Override
    public Class<IronCondorRequest> getSupportedType() {
        return IronCondorRequest.class;
    }
}
