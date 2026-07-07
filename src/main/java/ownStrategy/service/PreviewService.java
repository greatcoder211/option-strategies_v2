package ownStrategy.service;

import org.springframework.stereotype.Service;
import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.logic.finance.ChartGenerator;
import ownStrategy.logic.mapper.StrategyMapper;
import ownStrategy.logic.network.MarketDataClient;
import ownStrategy.model.strategy.OptionStrategy;

import java.util.List;

//unikanie "god object", rozdzielimy odpowiedzialność generowania pre-wykresów i zaglądania do fabryki właśnie tutaj
@Service
public class PreviewService {
    private final StrategyMapper strategyMapper;
    private final ChartGenerator chartGenerator;
    private final MarketDataClient marketDataClient;
    public PreviewService(StrategyMapper strategyMapper, ChartGenerator chartGenerator, MarketDataClient marketDataClient) {
        this.strategyMapper = strategyMapper;
        this.chartGenerator = chartGenerator;
        this.marketDataClient = marketDataClient;
    }
    public List<ChartPoint> processPreviewChart(Request request) {
        double spotPrice = marketDataClient.getStockPrice(request.getTicker());
        OptionStrategy domainStrategy = strategyMapper.mapToDomain(request);
        return chartGenerator.draw(spotPrice, domainStrategy);
    }
}