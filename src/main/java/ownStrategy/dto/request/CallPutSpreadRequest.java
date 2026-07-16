package ownStrategy.dto.request;

import ownStrategy.model.OptionType;
import ownStrategy.model.entity.portfolio.Request;

public abstract class CallPutSpreadRequest extends Request {
    private OptionType optionType;
}
