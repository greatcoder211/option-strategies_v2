package ownStrategy.dto.criteria;

import ownStrategy.model.entity.criteria.StrategySort;

import java.time.LocalDate;
import java.util.List;

public record FilterDTO(
        LocalDate createdAtFrom,
        LocalDate createdAtTo,
        List<String>strategies,
        String position,
        Boolean isCallPutStrategy,
        Boolean callOrPut,
        Boolean isTraditional,
        Boolean isBrokenLeg,
        String optionType,
        List<String> companiesConcat,
        LocalDate tradeDateFrom,
        LocalDate tradeDateTo,
        LocalDate expiryDateFrom,
        LocalDate expiryDateTo,
        String status,
        List<StrategySort> strategySortBy,
        int pageNumber,
        int pageSize
) {
}
