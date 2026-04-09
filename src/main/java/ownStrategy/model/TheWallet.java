package ownStrategy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ownStrategy.dto.Status;
import ownStrategy.logic.sPattern.OptionLeg;
import ownStrategy.logic.sPattern.OptionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//no comment
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "thewallet-nocomment")
@CompoundIndex(name = "user_ticker_date_idx", def = "{'userID': 1, 'ticker': 1, 'date': -1}")
public class TheWallet {
    @Id
    private String key;
    @Indexed
    private String userID;
    @Positive
    private int quant;
    private String strategyName;
    private String ticker;
    private double price;
    private OptionType type;
    private List<OptionLeg> legs = new ArrayList<>();
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Future
    private LocalDate expiry;
    private LocalDateTime date;
    private Status status;
    public TheWallet(String ticker, String strategyName, OptionType type, List<OptionLeg> legs, LocalDate expiry) {
        this.ticker = ticker;
        this.strategyName = strategyName;
        this.type = type;
        this.legs = legs;
        this.expiry = expiry;
    }
}
