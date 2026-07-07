package ownStrategy.logic.mapper;

import org.springframework.stereotype.Component;
import ownStrategy.dto.strategyPanel.Request;
import ownStrategy.model.strategy.OptionStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StrategyMapper {

    //request -> fabryka, która umie go obsługiwać
    private final Map<Class<? extends Request>, StrategyFactory<? extends Request>> registry = new HashMap<>();

    // wstrzykiwanie listy wszystkich fabryk
    public StrategyMapper(List<StrategyFactory<?>> factories) {
        for (StrategyFactory<?> factory : factories) {
            registry.put(factory.getSupportedType(), factory);
        }
    }

    @SuppressWarnings("unchecked")
    public OptionStrategy mapToDomain(Request request) {
        StrategyFactory<Request> factory = (StrategyFactory<Request>) registry.get(request.getClass());

        if (factory == null) {
            throw new IllegalArgumentException("Unsupported strategy type: " + request.getClass().getSimpleName());
        }

        return factory.create(request);
    }
}