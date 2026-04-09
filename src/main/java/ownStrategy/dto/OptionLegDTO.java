package ownStrategy.dto;

import ownStrategy.logic.sPattern.Belfort;
import ownStrategy.logic.sPattern.OptionType;

public record OptionLegDTO (
    double strikePrice,
    OptionType type,
    Belfort belfort
) {}
