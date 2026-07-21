package ownStrategy.logic.mapper.factory;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.IronButterflyRequestDTO;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.entity.request.IronButterflyRequest;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.IronButterfly;

import java.time.LocalDate;

@Component
public class IronButterflyFactory implements StrategyFactory<IronButterflyRequest> {
    @Override
    public OptionStrategy create(IronButterflyRequest request, double spotPrice) {
        if(request.getTradeDate() == null){
            request.setTradeDate(LocalDate.now());
        }
        return new IronButterfly(
                request.getQuantity(),
                request.getPosition(),
                request.getSpreadValue(),
                request.getTradeDate(),
                request.getExpiryDate(),
                spotPrice
        );
    }
    @Override
    public Class<IronButterflyRequest> getSupportedType() {
        return IronButterflyRequest.class;
    }
}
