package ownStrategy.dto.request;
import lombok.*;
import ownStrategy.model.OptionType;
import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class CallPutSpreadVerticalRequestDTO extends CallPutSpreadRequestDTO {
    OptionType optionType;
    double spreadValue;
    LocalDate tradeDate;
    LocalDate expiryDate;
}
