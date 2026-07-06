package ownStrategy.model;

import ownStrategy.dto.OptionType;

import java.time.LocalDate;

public record OptionLeg (int quantity,
                         Belfort position,
                         OptionType type,
                         double strikePrice,
                         LocalDate tradeDate,
                         LocalDate expiryDate){
    @Override
    public String toString() { return position + " " + type + " @" + strikePrice; }
}
