package ownStrategy.model.entity.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ownStrategy.model.OptionType;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class MultiStrikeMultiDateDiagonalRequest extends Request {
    private OptionType optionType;
    private List<Double> strikePrices;
    private LocalDate tradeDate;
    private LocalDate shortExpiryDate;
    private LocalDate longExpiryDate;
}