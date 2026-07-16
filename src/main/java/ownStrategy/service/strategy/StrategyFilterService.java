package ownStrategy.service.strategy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import ownStrategy.model.entity.criteria.Filter;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;
import ownStrategy.logic.PortfolioStrategyFilter;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.sort;

@Service
public class StrategyFilterService {
    private final PortfolioStrategyFilter portfolioStrategyFilter;
    private final MongoTemplate mongoTemplate;

    public StrategyFilterService(PortfolioStrategyFilter portfolioStrategyFilter, MongoTemplate mongoTemplate) {
        this.portfolioStrategyFilter = portfolioStrategyFilter;
        this.mongoTemplate = mongoTemplate;
    }
    public Page<PortfolioStrategy> filter(Filter filter) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (filter.getCreatedAtFrom() != null || filter.getCreatedAtTo() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByCreatedAtRange(filter.getCreatedAtFrom(), filter.getCreatedAtTo()));
        }

        if (filter.getPosition() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByPosition(filter.getPosition()));
        }

        if (filter.getStrategies() != null && !filter.getStrategies().isEmpty()) {
            criteriaList.add(portfolioStrategyFilter.filterByStrategies(filter.getStrategies()));
        }

        if (filter.getCompaniesConcat() != null && !filter.getCompaniesConcat().isEmpty()) {
            criteriaList.add(portfolioStrategyFilter.filterByCompanies(filter.getCompaniesConcat()));
        }

        if (filter.getIsCallPutStrategy() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByCallPutStrategies(filter.getIsCallPutStrategy(), filter.getCallOrPut(), filter.getOptionType()));
        }

        if (filter.getIsTraditional() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByTraditionalStrategies(filter.getIsTraditional()));
        }

        if (filter.getIsBrokenLeg() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByBrokenLegs(filter.getIsBrokenLeg()));
        }

        if (filter.getTradeDateFrom() != null || filter.getTradeDateTo() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByTradeDateRange(filter.getTradeDateFrom(), filter.getTradeDateTo()));
        }

        if (filter.getExpiryDateFrom() != null || filter.getExpiryDateTo() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByExpiryDateRange(filter.getExpiryDateFrom(), filter.getExpiryDateTo()));
        }

        if (filter.getStatus() != null) {
            criteriaList.add(portfolioStrategyFilter.filterByStatus(filter.getStatus()));
        }
        //złączenie wszystkich kryteriów
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        Sort sort = sort(PortfolioStrategy.class);
        Pageable pageable = PageRequest.of(filter.getPageNumber(), filter.getPageSize(), sort);

        query.with(pageable);
        List<PortfolioStrategy> list = mongoTemplate.find(query, PortfolioStrategy.class);

        return PageableExecutionUtils.getPage(list, pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), PortfolioStrategy.class));
    }

}
//stary komentarz odnośnie 'filter':
//logikę tego też w teorii trzebaby wynieść do osobnego interfejsu funkcjonalnego(vielleicht fabryka), ale już mi się nie chce z tym tak pierdolić(no nie), tym bardziej, że dużo iwęcej if-ów już tu nie wejdzie(no ile można tworzyć filtrów)