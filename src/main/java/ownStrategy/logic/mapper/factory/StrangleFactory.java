package ownStrategy.logic.mapper.factory;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.StrangleRequestDTO;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.entity.request.StrangleRequest;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.Strangle;

import java.time.LocalDate;

@Component
public class StrangleFactory implements StrategyFactory<StrangleRequest> {

    @Override
    public OptionStrategy create(StrangleRequest request, double spotPrice) {
        if(request.getTradeDate() == null){
            request.setTradeDate(LocalDate.now());
        }
        return new Strangle(
                request.getQuantity(),
                request.getPosition(),
                request.getSpreadValue(),
                request.getTradeDate(),
                request.getExpiryDate(),
                spotPrice
        );
    }

    @Override
    public Class<StrangleRequest> getSupportedType() {
        return StrangleRequest.class;
    }
}
