package ownStrategy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StrategyRequest {
    @NotEmpty
    @Size(min = 1)
    private List<@NotNull Double> spreads;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Future
    private LocalDate expiry;
}
