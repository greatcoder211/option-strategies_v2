package ownStrategy.dto;

import lombok.*;
import org.springframework.data.domain.Sort;
import ownStrategy.logic.sPattern.Belfort;
import ownStrategy.logic.sPattern.SpreadStrategy;

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
        private int page = 0;
        private int size = 10;
        public void setSize(int size) {
        this.size = Math.min(size, 100);
        }
}
