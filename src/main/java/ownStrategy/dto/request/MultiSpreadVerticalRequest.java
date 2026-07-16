package ownStrategy.dto.request;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ownStrategy.model.entity.portfolio.Request;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class MultiSpreadVerticalRequest extends Request {
    List<Double> spreadValues;
    LocalDate tradeDate;
    LocalDate expiryDate;
    double spotPrice;
}