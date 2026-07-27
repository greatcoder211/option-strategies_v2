package ownStrategy.controller.strategy;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionType;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.model.strategy.templates.diagonal.PoorMansCovered;
import ownStrategy.model.strategy.templates.vertical.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class OptionStrategyModelAssembler implements RepresentationModelAssembler<OptionStrategy, EntityModel<OptionStrategy>> {

    private String createEducationalLink(OptionStrategy optionStrategy){
        StringBuilder linkBuilder = new StringBuilder();
        if(optionStrategy instanceof  ButterflySpread && optionStrategy.getPosition().equals(Belfort.BUY)){
            linkBuilder.append("long-");
        }
        linkBuilder.append("https://www.tastylive.com/concepts-strategies/").append(formatCamelCaseToWords(optionStrategy.getClass().getSimpleName()));
        if(optionStrategy instanceof PoorMansCovered){
            PoorMansCovered poorMansCovered = (PoorMansCovered) optionStrategy;
            if(poorMansCovered.getOptionType().equals(OptionType.CALL)){
                linkBuilder.append("-call");
            }
            else if(poorMansCovered.getOptionType().equals(OptionType.PUT)){
                linkBuilder.append("-put");
            }
        }
        return linkBuilder.toString();
    }

    private String formatCamelCaseToWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("(?<=[a-z])(?=[A-Z])", "-").toLowerCase();
    }

    @Override
    public EntityModel<OptionStrategy> toModel(OptionStrategy entity) {
        EntityModel<OptionStrategy> model = EntityModel.of(entity);
        model.add(linkTo(StrategyController.class).withSelfRel());

        // Użycie nowej metody do wygenerowania dynamicznego linku
        String educationUrl = createEducationalLink(entity);
        model.add(Link.of(educationUrl).withRel("education"));

        return model;
    }
}
/*    private final Map<? extends OptionStrategy, List<String>> educationLinks = Map.of(
            ButterflySpread.class, List.of("https://www.tastylive.com/concepts-strategies/long-butterfly-spread"),
            CalendarSpread.class, List.of("https://www.tastylive.com/concepts-strategies/calendar-spread"),
            IronButterfly.class, List.of("https://www.tastylive.com/concepts-strategies/iron-butterfly"),
            IronCondor.class, List.of("https://www.tastylive.com/concepts-strategies/iron-condor"),
            PoorMansCovered.class, List.of("https://www.tastylive.com/concepts-strategies/poor-man-covered-call", "https://www.tastylive.com/concepts-strategies/poor-man-covered-put"),
            Strangle.class, List.of("https://www.tastylive.com/concepts-strategies/strangle"),
            RatioSpread.class, List.of("https://www.tastylive.com/concepts-strategies/ratio-spread"),
            VerticalSpread.class, List.of("https://www.tastylive.com/concepts-strategies/vertical-spread")
    );*/