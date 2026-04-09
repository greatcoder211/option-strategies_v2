package ownStrategy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ownStrategy.dto.CompanyDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "search_history")
public class SearchHistory {
    @Id
    private String id;
    private String keyword;
    private LocalDateTime date;
    private int resultsCount;
    private List<CompanyDTO> companies;
    public SearchHistory(String keyword, int resultsCount) {
        this.keyword = keyword;
        this.resultsCount = resultsCount;
        this.date = LocalDateTime.now();
    }
}
