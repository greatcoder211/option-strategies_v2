package ownStrategy.model.entity.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ownStrategy.model.OptionType;

import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class CallPutSpreadAsymmetricalRequest extends CallPutSpreadRequest {
    OptionType optionType;
    double spreadValue;
    LocalDate tradeDate;
    LocalDate expiryDate;
}
