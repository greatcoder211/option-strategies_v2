package ownStrategy.controller.strategy;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.asymmetrical.RatioSpread;
import ownStrategy.model.strategy.templates.diagonal.PoorMansCovered;
import ownStrategy.model.strategy.templates.horizontal.CalendarSpread;
import ownStrategy.model.strategy.templates.vertical.*;
import java.util.List;
import java.util.Map;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class StrategyModelAssembler implements RepresentationModelAssembler<PortfolioStrategy, EntityModel<OptionStrategy>> {

    // Słownik z linkami edukacyjnymi dla konkretnych typów strategii
    private final Map<String, List<String>> educationLinks = Map.of(
            "Long Butterfly Spread", List.of("https://www.tastylive.com/concepts-strategies/long-butterfly-spread"),
            //reszta klasa do poprawy
            CalendarSpread.class, List.of("https://www.tastylive.com/concepts-strategies/calendar-spread"),
            IronButterfly.class, List.of("https://www.tastylive.com/concepts-strategies/iron-butterfly"),
            IronCondor.class, List.of("https://www.tastylive.com/concepts-strategies/iron-condor"),
            PoorMansCovered.class, List.of("https://www.tastylive.com/concepts-strategies/poor-man-covered-call", "https://www.tastylive.com/concepts-strategies/poor-man-covered-put"),
            Strangle.class, List.of("https://www.tastylive.com/concepts-strategies/strangle"),
            RatioSpread.class, List.of("https://www.tastylive.com/concepts-strategies/ratio-spread"),
            VerticalSpread.class, List.of("https://www.tastylive.com/concepts-strategies/vertical-spread")
    );
   @Override
    public EntityModel<PortfolioStrategy> toModel(PortfolioStrategy entity) {
        EntityModel<PortfolioStrategy> model = EntityModel.of(entity);
        model.add(linkTo(StrategyController.class).withSelfRel());
        List<String> educationUrls = educationLinks.get(entity.getClass());
        if (educationUrls != null) {
            for (String url : educationUrls) {
                model.add(Link.of(url).withRel("education"));
            }
        }
        return model;
    }
}