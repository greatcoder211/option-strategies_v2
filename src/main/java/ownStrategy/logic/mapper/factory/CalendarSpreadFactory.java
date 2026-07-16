package ownStrategy.logic.mapper.factory;

import ownStrategy.dto.request.CalendarSpreadRequest;
import ownStrategy.logic.mapper.StrategyFactory;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.horizontal.CalendarSpread;

public class CalendarSpreadFactory implements StrategyFactory<CalendarSpreadRequest> {
    @Override
    public OptionStrategy create(CalendarSpreadRequest request) {
        return new CalendarSpread(
                request.getQuantity(),
                request.getPosition(),
                request.getOptionType(),
                request.getStrikePrice(),
                request.getTradeDate(),
                request.getShortExpiryDate(),
                request.getLongExpiryDate(),
                request.getSpotPrice()
        );
    }
    @Override
    public Class<CalendarSpreadRequest> getSupportedType() {
        return CalendarSpreadRequest.class;
    }
}
