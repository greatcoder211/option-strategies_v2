package ownStrategy.dto.strategyPanel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ownStrategy.dto.OptionType;

import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class CallPutSpreadAsymmetricalRequest extends Request {
    OptionType optionType;
    double spreadValue;
    LocalDate tradeDate;
    LocalDate expiryDate;
    double spotPrice;
}
