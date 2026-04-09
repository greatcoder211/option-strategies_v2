package ownStrategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    private String ticker;
    private String name;
    private String region;
}
    /*    public CompanyDTO(String ticker, String name, String region) {
        this.ticker = ticker;
        this.name = name;
        this.region = region;
    }
    public String getTicker() {
        return ticker;
    }
    public String getName() {
        return name;
    }
    public String getRegion() {
        return region;
    }

 */

