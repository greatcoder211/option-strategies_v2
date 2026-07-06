package ownStrategy.dto;
import lombok.*;
import ownStrategy.config.StrategiesPage;
import ownStrategy.model.Belfort;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterDTO {
        private List<String> strategies;
        private SpreadType spreadType;
        private Belfort position;
        private List<String> companiesConcat;
        private Status status;
        private LocalDate expiryFrom;
        private LocalDate expiryTo;
        private List<SortDTO> sortBy;
        private StrategiesPage strategiesPage;
        private int page = 0;
        private int size = 10;
}
//tu zostaje klsa(trochę bardziej złożone)