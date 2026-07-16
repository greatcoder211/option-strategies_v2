package ownStrategy.dto.portfolio;
import ownStrategy.model.*;
import ownStrategy.model.entity.portfolio.Company;
import ownStrategy.model.entity.portfolio.OptionLeg;

import java.time.LocalDate;
import java.util.List;

//głupi kurier- przepisanie danych, zero hermetyzacji
public record PortfolioStrategyDTO (
    LocalDate createdAt,
    String portfolioStrategyID,
    String userID,
    int quantity,
    Belfort position,
    OptionType optionType,
    String strategyName,
    Company company,
    Double spotPrice,
    List<OptionLeg> optionLegs,
    Status status){}