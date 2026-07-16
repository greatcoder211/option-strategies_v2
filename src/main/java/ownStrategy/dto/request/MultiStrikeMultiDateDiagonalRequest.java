package ownStrategy.dto.request;

import lombok.*;
import ownStrategy.model.OptionType;
import ownStrategy.model.entity.portfolio.Request;

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