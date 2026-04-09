package ownStrategy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SortDTO {
    private String field;      // np. "ticker", "expiry", "score"
    private String direction;  // "ASC" lub "DESC"
}
