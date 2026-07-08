package ownStrategy.dto.strategyPanel;
import lombok.*;
import ownStrategy.dto.OptionType;
import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class CallPutSpreadVerticalRequest extends CallPutSpreadRequest {
    OptionType optionType;
    double spreadValue;
    LocalDate tradeDate;
    LocalDate expiryDate;
    double spotPrice;
}
