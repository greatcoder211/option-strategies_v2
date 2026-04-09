package ownStrategy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "favorites")
public class Favorites {
    @Id
    private String id;
    private String ticker;
    private LocalDateTime addedAt;
    public Favorites(String ticker){
        this.ticker = ticker;
        this.addedAt = LocalDateTime.now();
    }
}
