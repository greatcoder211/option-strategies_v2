package ownStrategy.dto.request;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ownStrategy.dto.portfolio.CompanyDTO;
import ownStrategy.model.Belfort;
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "Strategy Type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ButterflySpreadRequestDTO.class, name = "Butterfly Spread"),
        @JsonSubTypes.Type(value = CalendarSpreadRequestDTO.class, name = "Calendar Spread"),
        @JsonSubTypes.Type(value = IronButterflyRequestDTO.class, name = "Iron Butterfly"),
        @JsonSubTypes.Type(value = IronCondorRequestDTO.class, name = "Iron Condor"),
        @JsonSubTypes.Type(value = PoorMansCoveredRequestDTO.class, name = "Poor Mans Covered"),
        @JsonSubTypes.Type(value = RatioSpreadRequestDTO.class, name = "Ratio Spread"),
        @JsonSubTypes.Type(value = StrangleRequestDTO.class, name = "Strangle"),
        @JsonSubTypes.Type(value = VerticalSpreadRequestDTO.class, name = "Vertical Spread")
})
public abstract class RequestDTO {
    @Min(1)
    int quantity;
    @NotNull
    Belfort position;
    @NotBlank
    String strategyName;
    @NotBlank
    CompanyDTO selectedCompany;
}