package ownStrategy.logic.network;

import org.springframework.stereotype.Component;

@Component
public interface PriceClient {
    double getStockPrice(String ticker);
}
