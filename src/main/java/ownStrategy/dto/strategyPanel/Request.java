package ownStrategy.dto.strategyPanel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ownStrategy.dto.OptionType;
import ownStrategy.model.Belfort;
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Request {
    @Min(1)
    private int quantity;
    @NotNull
    private Belfort position;
    @NotNull
    private OptionType callPutVariant;
    @NotBlank
    private String strategyName;
    //front-end dostarczy ticker- wypadkową key i selection(choice)
    @NotBlank
    private String ticker;
    //tu kończymy- ceny, spready, daty- wszystko to jest wypadkową tego co gramy(propercje naszych dzieci)
}
