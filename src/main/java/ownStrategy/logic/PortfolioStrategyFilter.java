package ownStrategy.logic;

import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.Positive;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import ownStrategy.dto.Company;
import ownStrategy.dto.OptionType;
import ownStrategy.dto.Status;
import ownStrategy.dto.StrategySort;
import ownStrategy.model.Belfort;
import ownStrategy.model.OptionLeg;

import java.time.LocalDate;
import java.util.List;
@Component
public class PortfolioStrategyFilter {
    private LocalDate createdAt;
    @Positive
    private int quantity;
    private Belfort position;
    @Nullable
    private OptionType optionType;
    private String strategyName;
    private Company company;
    //Double, by odpowiednio opakować to nullem w przypadku strategii "na później"
    private Double spotPrice;
    private List<OptionLeg> optionLegs;
    //OPEN, CLOSE, PENDING
    private Status status;

    public Criteria filterByCreatedAtRange(LocalDate createdAtFrom, LocalDate createdAtTo) {
        if (createdAtFrom == null && createdAtTo == null) {
            return new Criteria();
        }
        Criteria criteria = Criteria.where("createdAt");
        if (createdAtFrom != null && createdAtTo != null) {
            return criteria.gte(createdAtFrom).lte(createdAtTo);
        }
        return createdAtFrom != null ? criteria.gte(createdAtFrom) : criteria.lte(createdAtTo);
    }

    public Criteria filterByPosition(String position) {
        if (("BUY").equals(optionType)) {
            return Criteria.where("position").is("BUY");
        } else if (("SELL").equals(optionType)) {
            return Criteria.where("position").is("SELL");
        } else return new Criteria();
    }

    public Criteria filterByOptionType(String optionType) {
        if (("CALL").equals(optionType)) {
            return Criteria.where("optionType").is("CALL");
        } else if (("PUT").equals(optionType)) {
            return Criteria.where("optionType").is("PUT");
        } else return new Criteria();
    }

    public Criteria filterByCallPutStrategies(Boolean isCallPutOnly, Boolean callOrPut, String optionType) {
        if (isCallPutOnly == null || !isCallPutOnly) {
            return new Criteria();
        }
        if (callOrPut == null || !callOrPut) {
            return Criteria.where("optionType").ne(null);
        } else {
            return filterByOptionType(optionType);
        }
    }

    public Criteria filterByTraditionalStrategies(Boolean isTraditional) {
        if (isTraditional == null || !isTraditional) {
            return new Criteria();
        }
        return Criteria.where("optionType").is(null);
    }

    public Criteria filterByStrategies(List<String> strategyNames) {
        if (strategyNames == null || strategyNames.isEmpty()) {
            return new Criteria();
        }
        return Criteria.where("strategyName").in(strategyNames);
    }
//tu było 'filterBySpreadType', można pomyśleć, czy jakoś nie podzielić strategie na "proste"(np. vertical) i "złożone"(np. condor, pmcc/pmpcp) ale nwm chyba nie

    //ta metoda do poprawy- nie zadziała pod względem logiki, muszę gucknąć wpierw jak wygląda reprezentacja firmy na froncie
    public Criteria filterByCompanies(List<String> companies) {
        if (companies == null || companies.isEmpty()) {
            return new Criteria();
        }
        return Criteria.where("companyConcat").in(companies);
    }

    public Criteria filterByBrokenLegs(Boolean isBrokenLeg) {
        if (isBrokenLeg == null || !isBrokenLeg) {
            return new Criteria();
        }
        String exprJson = "{ $ne: [ { $max: '$optionLegs.quantity' }, { $min: '$optionLegs.quantity' } ] }";
        return Criteria.where("$expr").is(Document.parse(exprJson));
    }

    //dwie poniższe metody: jeżeli którakolwiek z nóg łapie się do przedziału, to ją bierzemy
    public Criteria filterByTradeDateRange(LocalDate tradeDateFrom, LocalDate tradeDateTo) {
        if (tradeDateFrom == null && tradeDateTo == null) {
            return new Criteria();
        }
        Criteria criteria = Criteria.where("optionLegs.tradeDate");
        if (tradeDateFrom != null && tradeDateTo != null) {
            return criteria.gte(tradeDateFrom).lte(tradeDateTo);
        }
        return tradeDateFrom != null ? criteria.gte(tradeDateFrom) : criteria.lte(tradeDateTo);
    }

    public Criteria filterByExpiryDateRange(LocalDate expiryDateFrom, LocalDate expiryDateTo) {
        if (expiryDateFrom == null && expiryDateTo == null) {
            return new Criteria();
        }
        Criteria criteria = Criteria.where("optionLegs.expiryDate");
        if (expiryDateFrom != null && expiryDateTo != null) {
            return criteria.gte(expiryDateFrom).lte(expiryDateTo);
        }
        return expiryDateFrom != null ? criteria.gte(expiryDateFrom) : criteria.lte(expiryDateTo);
    }

    public Criteria filterByStatus(String status) {
        if (status == null) {
            return new Criteria();
        }
        return Criteria.where("status").is(status);
    }

    public Sort getSort(List<StrategySort> clientStrategySortList) {
        if (clientStrategySortList == null || clientStrategySortList.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        Sort finalSort = null;
        for (StrategySort clientStrategySort : clientStrategySortList) {
            String mongoField = switch (clientStrategySort.field()) {
                case "quantity" -> "quantity";
                case "spotPrice" -> "spotPrice";
                case "tradeDate" -> "optionLegs.tradeDate";
                case "expiry" -> "optionLegs.expiryDate";
                default -> "createdAt";
            };
            Sort.Direction direction = "DESC".equalsIgnoreCase(clientStrategySort.direction())
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;
            Sort currentSort = Sort.by(direction, mongoField);
            if (finalSort == null) {
                finalSort = currentSort;
            } else {
                finalSort = finalSort.and(currentSort);
            }
        }

        return finalSort;
    }

    private Sort mapSingleSort(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return Sort.unsorted();
        }
        return switch (sortBy) {
            case "lowestQuantity" -> sortByLowestQuantity();
            case "highestQuantity" -> sortByHighestQuantity();
            case "lowestSpotPrice" -> sortByLowestSpotPrice();
            case "highestSpotPrice" -> sortByHighestSpotPrice();
            case "earliestTradeDate" -> sortByEarliestTradeDate();
            case "latestTradeDate" -> sortByLatestTradeDate();
            case "earliestExpiryDate" -> sortByEarliestExpiryDate();
            case "latestExpiryDate" -> sortByLatestExpiryDate();
            //pusty sort- dla nierozpoznanych
            default -> Sort.unsorted();
        };
    }

    private Sort sortByLowestQuantity() {
        return Sort.by(Sort.Direction.ASC, "quantity");
    }

    private Sort sortByHighestQuantity() {
        return Sort.by(Sort.Direction.DESC, "quantity");
    }

    private Sort sortByLowestSpotPrice() {
        return Sort.by(Sort.Direction.ASC, "spotPrice");
    }

    private Sort sortByHighestSpotPrice() {
        return Sort.by(Sort.Direction.DESC, "spotPrice");
    }

    private Sort sortByEarliestTradeDate() {
        return Sort.by(Sort.Direction.ASC, "optionLegs.tradeDate"); // Poprawione L
    }

    private Sort sortByLatestTradeDate() {
        return Sort.by(Sort.Direction.DESC, "optionLegs.tradeDate"); // Poprawione L
    }

    private Sort sortByEarliestExpiryDate() {
        return Sort.by(Sort.Direction.ASC, "optionLegs.expiryDate"); // Dodana ścieżka
    }

    private Sort sortByLatestExpiryDate() {
        return Sort.by(Sort.Direction.DESC, "optionLegs.expiryDate"); // Dodana ścieżka
    }
}
