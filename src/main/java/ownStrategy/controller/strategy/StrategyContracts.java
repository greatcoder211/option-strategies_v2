package ownStrategy.controller.strategy;

import ownStrategy.dto.request.RequestDTO;
import ownStrategy.model.entity.portfolio.ChartPoint;
import ownStrategy.model.entity.request.Request;

import java.security.Principal;
import java.util.List;

public class StrategyContracts {
    //to może zostać, to jest jakby tutaj okej
    public record ScoreChartResponse(List<ChartPoint> chartPoints, ChartPoint currentPriceMarker) {}
    public record PreviewChartResponse(String strategyName, List<ChartPoint> chartPoints){}
}
