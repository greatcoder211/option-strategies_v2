package ownStrategy.dto.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class PoorMansCoveredRequestDTO extends MultiStrikeMultiDateDiagonalRequestDTO {
}
