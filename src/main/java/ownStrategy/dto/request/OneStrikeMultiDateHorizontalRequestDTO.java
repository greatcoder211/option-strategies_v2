package ownStrategy.dto.request;

import lombok.*;
import ownStrategy.model.OptionType;

import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class OneStrikeMultiDateHorizontalRequestDTO extends RequestDTO {
    OptionType optionType;
    double strikePrice;
    LocalDate tradeDate;
    LocalDate shortExpiryDate;
    LocalDate longExpiryDate;
}
