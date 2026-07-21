package ownStrategy.logic.mapper.factory;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.VerticalSpreadRequestDTO;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.entity.request.VerticalSpreadRequest;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.vertical.VerticalSpread;

import java.time.LocalDate;

@Component
public class VerticalSpreadFactory implements StrategyFactory <VerticalSpreadRequest>{
    @Override
    public OptionStrategy create(VerticalSpreadRequest request, double spotPrice) {
        if(request.getTradeDate() == null){
            request.setTradeDate(LocalDate.now());
        }
        return new VerticalSpread(
                request.getQuantity(),
                request.getPosition(),
                request.getOptionType(),
                request.getSpreadValue(),
                request.getTradeDate(),
                request.getExpiryDate(),
                spotPrice
        );
    }

    @Override
    public Class<VerticalSpreadRequest> getSupportedType() {
        return VerticalSpreadRequest.class;
    }
}
