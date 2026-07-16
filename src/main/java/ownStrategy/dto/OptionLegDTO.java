package ownStrategy.dto;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionType;
import java.time.LocalDate;
public record OptionLegDTO(int quantity,
                           Belfort position,
                           OptionType type,
                           double strikePrice,
                           LocalDate tradeDate,
                           LocalDate expiryDate){
    @Override
    public String toString() {return position + " " + type + " @" + strikePrice;}
}
