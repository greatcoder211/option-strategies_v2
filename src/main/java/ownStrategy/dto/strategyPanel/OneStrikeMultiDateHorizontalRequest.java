package ownStrategy.dto.strategyPanel;

import lombok.*;
import ownStrategy.dto.OptionType;
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
