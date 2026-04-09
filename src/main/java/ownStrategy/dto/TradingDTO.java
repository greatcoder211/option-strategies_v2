package ownStrategy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import ownStrategy.logic.sPattern.Belfort;
import ownStrategy.logic.sPattern.OptionLeg;
import ownStrategy.logic.sPattern.OptionType;
import ownStrategy.logic.sPattern.SpreadStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TradingDTO {
    @Min(1)
    private int quant;
    private SpreadStrategy strategy;
    //front-end dostarczy ticker- wypadkową key i selection(choice)
    private String ticker;
    private OptionType optionType;
    @NotEmpty
    @Size(min = 1)
    private List<@NotNull Double> spreads;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Future
    private LocalDate expiry;
    //The Wallet- no comment
    private String userID;
}
