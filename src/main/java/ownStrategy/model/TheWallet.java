package ownStrategy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ownStrategy.dto.Status;
import ownStrategy.dto.OptionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "thewallet-nocomment")
@CompoundIndex(name = "user_ticker_date_idx", def = "{'userID': 1, 'ticker': 1, 'date': -1}")
public class TheWallet {
    @Id
    private String walletKey;
    @Indexed
    private String userID;
    @Positive
    private int quantity;
    private String strategyName;
    private String ticker;
    private double price;
    private OptionType optionType;
    private List<OptionLeg> optionLegs;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @FutureOrPresent
    private LocalDate tradeDate;
    @Future
    private LocalDate expiryDate;
    //OPEN, CLOSE
    private Status status;
    public TheWallet(String ticker, String strategyName, OptionType optionType, List<OptionLeg> optionLegs, LocalDate expiryDate) {
        this.ticker = ticker;
        this.strategyName = strategyName;
        this.optionType = optionType;
        this.optionLegs = optionLegs;
        this.expiryDate = expiryDate;
    }
}
