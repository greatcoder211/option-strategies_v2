package ownStrategy.service;

import org.springframework.stereotype.Service;
import ownStrategy.dto.ChartPoint;

import java.util.List;

//utworzony, by strategie bezpośrednio nie grzebały w beanach(Chart, Chartgenerator), bo tak nie powinno się robić
@Service
public class StrategyService {
    public List<ChartPoint> calculatePreviewChart(double spotPrice) {

    }
}
