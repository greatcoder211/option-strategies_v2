package ownStrategy.logic.mapper.factory;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.RatioSpreadRequestDTO;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.entity.request.RatioSpreadRequest;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.asymmetrical.RatioSpread;

import java.time.LocalDate;

@Component
public class RatioSpreadFactory implements StrategyFactory<RatioSpreadRequest> {

    @Override
    public OptionStrategy create(RatioSpreadRequest request, double spotPrice) {
        if(request.getTradeDate() == null){
            request.setTradeDate(LocalDate.now());
        }
        return new RatioSpread(
                request.getQuantity(),
                request.getPosition(),
                request.getOptionType(),
                request.getSpreadValue(),
                spotPrice,
                request.getTradeDate(),
                request.getExpiryDate()
        );
    }
    @Override
    public Class<RatioSpreadRequest> getSupportedType() {
        return RatioSpreadRequest.class;
    }
}
