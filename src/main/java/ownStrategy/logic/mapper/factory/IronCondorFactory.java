package ownStrategy.logic.mapper.factory;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.IronCondorRequestDTO;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.entity.request.IronCondorRequest;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.IronCondor;

import java.time.LocalDate;

@Component
public class IronCondorFactory implements StrategyFactory <IronCondorRequest>{
    @Override
    public OptionStrategy create(IronCondorRequest request, double spotPrice) {
        if(request.getTradeDate() == null){
            request.setTradeDate(LocalDate.now());
        }
        return new IronCondor(
                request.getQuantity(),
                request.getPosition(),
                request.getSpreadValues(),
                request.getTradeDate(),
                request.getExpiryDate(),
                spotPrice
        );
    }
    @Override
    public Class<IronCondorRequest> getSupportedType() {
        return IronCondorRequest.class;
    }
}
