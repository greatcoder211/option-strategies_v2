package ownStrategy.model.entity.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ownStrategy.dto.request.CallPutSpreadVerticalRequestDTO;

@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class ButterflySpreadRequest extends CallPutSpreadVerticalRequest {
}
