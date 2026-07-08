package ownStrategy.dto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Filter {
        private LocalDate createdAtFrom;
        private LocalDate createdAtTo;
        private List<String> strategies;
        private String position;
        private Boolean isCallPutStrategy;
        private Boolean callOrPut;
        private Boolean isTraditional;
        private Boolean isBrokenLeg;
        private String optionType;
        private List<String> companiesConcat;
        private LocalDate tradeDateFrom;
        private LocalDate tradeDateTo;
        private LocalDate expiryDateFrom;
        private LocalDate expiryDateTo;
        private String status;
        private List<StrategySort> strategySortBy;
        private int page = 0;
        private int pageSize;
}
//tu zostaje klsa(trochę bardziej złożone)