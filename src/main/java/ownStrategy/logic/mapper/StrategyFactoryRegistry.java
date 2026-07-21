package ownStrategy.logic.mapper;

import org.springframework.stereotype.Component;
import ownStrategy.dto.request.RequestDTO;
import ownStrategy.model.entity.request.Request;
import ownStrategy.model.strategy.OptionStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StrategyFactoryRegistry {

    //request -> fabryka, która umie go obsługiwać, pracujemy na ORYGINAŁACH
    private final Map<Class<? extends Request>, StrategyFactory<? extends Request>> registry = new HashMap<>();

    // wstrzykiwanie listy wszystkich fabryk
    public StrategyFactoryRegistry(List<StrategyFactory<?>> factories) {
        for (StrategyFactory<?> factory : factories) {
            registry.put(factory.getSupportedType(), factory);
        }
    }

    @SuppressWarnings("unchecked")
    public OptionStrategy mapToDomain(Request request, double spotPrice) {
        StrategyFactory<Request> factory = (StrategyFactory<Request>) registry.get(request.getClass());

        if (factory == null) {
            throw new IllegalArgumentException("Unsupported strategy type: " + request.getClass().getSimpleName());
        }

        return factory.create(request, spotPrice);
    }
}