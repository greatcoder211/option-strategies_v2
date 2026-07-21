package ownStrategy.logic.mapper.factory;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.CalendarSpreadRequestDTO;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.entity.request.CalendarSpreadRequest;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.horizontal.CalendarSpread;
@Component
public class CalendarSpreadFactory implements StrategyFactory<CalendarSpreadRequest> {
    @Override
    public OptionStrategy create(CalendarSpreadRequest request, double spotPrice) {
        return new CalendarSpread(
                request.getQuantity(),
                request.getPosition(),
                request.getOptionType(),
                request.getStrikePrice(),
                request.getTradeDate(),
                request.getShortExpiryDate(),
                request.getLongExpiryDate(),
                spotPrice
        );
    }
    @Override
    public Class<CalendarSpreadRequest> getSupportedType() {
        return CalendarSpreadRequest.class;
    }
}
