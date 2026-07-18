package ownStrategy.logic.mapper.factory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ownStrategy.dto.request.PoorMansCoveredRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionType;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.diagonal.PoorMansCovered;

import java.time.LocalDate;
import java.util.List;

public class PoorMansCoveredFactory implements StrategyFactory<PoorMansCoveredRequest> {

    @Override
    public OptionStrategy create(PoorMansCoveredRequest request) {
        return new PoorMansCovered(
                request.getQuantity(),
                request.getPosition(),
                request.getOptionType(),
                request.getStrikePrices(),
                request.getTradeDate(),
                request.getShortExpiryDate(),
                request.getLongExpiryDate(),
                request.getSpotPrice()
        );
    }
    @Override
    public Class<PoorMansCoveredRequest> getSupportedType() {
        return PoorMansCoveredRequest.class;
    }
}
