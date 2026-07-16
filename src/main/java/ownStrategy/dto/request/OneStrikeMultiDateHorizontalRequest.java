package ownStrategy.dto.request;

import lombok.*;
import ownStrategy.model.OptionType;
import ownStrategy.model.entity.portfolio.Request;

import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class OneStrikeMultiDateHorizontalRequest extends Request {
    OptionType optionType;
    double strikePrice;
    LocalDate tradeDate;
    LocalDate shortExpiryDate;
    LocalDate longExpiryDate;
    double spotPrice;
}
