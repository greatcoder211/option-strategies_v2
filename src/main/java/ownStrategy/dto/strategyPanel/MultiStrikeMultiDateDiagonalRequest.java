package ownStrategy.dto.strategyPanel;

import lombok.*;
import ownStrategy.dto.OptionType;
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
    private double spotPrice;
}