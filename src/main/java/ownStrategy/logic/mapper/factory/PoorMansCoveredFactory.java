package ownStrategy.logic.mapper.factory;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.PoorMansCoveredRequestDTO;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.entity.request.PoorMansCoveredRequest;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.diagonal.PoorMansCovered;

import java.time.LocalDate;

@Component
public class PoorMansCoveredFactory implements StrategyFactory<PoorMansCoveredRequest> {

    @Override
    public OptionStrategy create(PoorMansCoveredRequest request, double spotPrice) {
        return new PoorMansCovered(
                request.getQuantity(),
                request.getPosition(),
                request.getOptionType(),
                request.getStrikePrices(),
                request.getTradeDate(),
                request.getShortExpiryDate(),
                request.getLongExpiryDate(),
                spotPrice
        );
    }
    @Override
    public Class<PoorMansCoveredRequest> getSupportedType() {
        return PoorMansCoveredRequest.class;
    }
}
