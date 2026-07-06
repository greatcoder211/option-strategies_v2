package ownStrategy.dto;
import java.time.LocalDate;
public record OptionLegSnippet(double strikePrice, LocalDate tradeDate, LocalDate expiryDate){ }
