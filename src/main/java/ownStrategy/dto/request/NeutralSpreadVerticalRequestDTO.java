package ownStrategy.dto.request;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public abstract class NeutralSpreadVerticalRequestDTO extends RequestDTO {
    double spreadValue;
    LocalDate tradeDate;
    LocalDate expiryDate;
}
