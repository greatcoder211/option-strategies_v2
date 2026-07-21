package ownStrategy.model.entity.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class NeutralSpreadVerticalRequest extends Request {
    double spreadValue;
    LocalDate tradeDate;
    LocalDate expiryDate;
}
