package ownStrategy.model.entity.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ownStrategy.model.Belfort;
import ownStrategy.model.entity.portfolio.Company;

@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Request {
    @Min(1)
    private int quantity;
    @NotNull
    private Belfort position;
    @NotBlank
    private String strategyName;
    @NotBlank
    private Company selectedCompany;
//tickera już tu nie powinno być- wynika z logiki biznesowej, a nie jest czystym parametrem zapytania użytkownila   @NotBlank
//    private String ticker;
    //tu kończymy- ceny, spready, daty- wszystko to jest wypadkową tego co gramy(propercje naszych dzieci)
}
