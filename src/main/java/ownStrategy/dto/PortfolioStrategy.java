package ownStrategy.dto;
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
import ownStrategy.model.strategy.OptionStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@Document(collection = "bedzie-sanfrancisco")
@CompoundIndex(name = "user_ticker_date_idx", def = "{'userID': 1, 'ticker': 1, 'date': -1}")//nie pamietam juz o co tu chodzilo
public class PortfolioStrategy {
    @Id
    private String walletKey;
    @Indexed
    private String userID;
    private OptionStrategy optionStrategy;
    //OPEN, CLOSE
    private Status status;
}
