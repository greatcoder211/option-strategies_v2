package ownStrategy.dto.strategyPanel;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class NeutralSpreadVerticalRequest extends Request {
    double spreadValue;
    LocalDate tradeDate;
    LocalDate expiryDate;
    double spotPrice;
}
