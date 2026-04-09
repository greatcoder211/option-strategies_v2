package ownStrategy.logic;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import ownStrategy.dto.SpreadType;
import ownStrategy.dto.Status;
import ownStrategy.logic.sPattern.Belfort;

import java.time.LocalDate;
import java.util.List;
@Component
public class WalletFilter {

    //1. Criteria
    public Criteria filterByStrategies(List<String> strategies) {
        if (strategies == null || strategies.isEmpty()) {
            return new Criteria();
        }
        return Criteria.where("strategyName").in(strategies);
    }

    public Criteria filterBySpreadType(SpreadType spreadType) {
        if (spreadType == null) {
            return new Criteria();
        }
        return Criteria.where("spreadType").is(spreadType);
    }

    public Criteria filterByPosition(Belfort position) {
        if (position == null) {
            return new Criteria();
        }
        return Criteria.where("position").is(position);
    }

    public Criteria filterByCompanies(List<String> companies) {
        if (companies == null || companies.isEmpty()) {
            return new Criteria();
        }
        return Criteria.where("companyConcat").in(companies);
    }

    public Criteria filterByStatus(Status status) {
        if (status == null) {
            return new Criteria();
        }
        return Criteria.where("status").is(status);
    }

    public Criteria filterByExpiryRange(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return new Criteria();
        }
        Criteria criteria = Criteria.where("expiry");
        if (from != null && to != null) {
            return criteria.gte(from).lte(to);
        }
        return from != null ? criteria.gte(from) : criteria.lte(to);
    }

    //2. Sort
    public Sort sortByLatestTradeDate() {
        return Sort.by(Sort.Direction.DESC, "date");
    }

    public Sort sortByEarliestTradeDate() {
        return Sort.by(Sort.Direction.ASC, "date");
    }

    public Sort sortByHighestQuantity() {
        return Sort.by(Sort.Direction.DESC, "quant");
    }

    public Sort sortByLowestQuantity() {
        return Sort.by(Sort.Direction.ASC, "quant");
    }

    public Sort sortByHighestPrice() {
        return Sort.by(Sort.Direction.DESC, "price");
    }

    public Sort sortByLowestPrice() {
        return Sort.by(Sort.Direction.ASC, "price");
    }

    public Sort sortByFastestExpiry() {
        return Sort.by(Sort.Direction.ASC, "expiry");
    }

    public Sort sortByLatestExpiry() {
        return Sort.by(Sort.Direction.DESC, "expiry");
    }
}
