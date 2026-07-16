package ownStrategy.dto.request;
import lombok.*;
import ownStrategy.model.entity.portfolio.Request;

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
