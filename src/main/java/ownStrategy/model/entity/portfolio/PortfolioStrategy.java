package ownStrategy.model.entity.portfolio;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionType;
import ownStrategy.model.Status;

import java.time.LocalDate;
import java.util.List;
@Getter @Setter @NoArgsConstructor
//@Document(collection = "bedzie-sanfrancisco")
//@CompoundIndex(name = "user_ticker_date_idx", def = "{'userID': 1, 'ticker': 1, 'date': -1}")//nie pamietam juz o co tu chodzilo
public class PortfolioStrategy {
    private LocalDate createdAt;
    @Id
    private String portfolioStrategyID;
    //userID to de facto ID całego portfolio(bo jeden user może mieć tylko jedno portfolio, czyli listę strategii PortfolioStrategy)
    @Indexed
    private String userID;
    @Positive
    private int quantity;
    private Belfort position;
    @Nullable
    private OptionType optionType;
    private String strategyName;
    private Company company;
    //Double, by odpowiednio opakować to nullem w przypadku strategii "na później"
    private Double spotPrice;
    private List<OptionLeg> optionLegs;
    //OPEN, CLOSE, PENDING
    private Status status;
    @Builder
    public PortfolioStrategy(int quantity, Belfort position, @Nullable OptionType optionType, String strategyName, Company company, Double spotPrice, List<OptionLeg> optionLegs, Status status) {
        this.createdAt = LocalDate.now();
        this.quantity = quantity;
        this.position = position;
        this.optionType = optionType;
        this.strategyName = strategyName;
        this.company = company;
        this.spotPrice = spotPrice;
        this.optionLegs = optionLegs;
        this.status = status;
    }
}
