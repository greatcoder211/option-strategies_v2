package ownStrategy.model.entity.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class RatioSpreadRequest extends CallPutSpreadAsymmetricalRequest {
}
