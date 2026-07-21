package ownStrategy.model.entity.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ownStrategy.model.OptionType;

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
}
