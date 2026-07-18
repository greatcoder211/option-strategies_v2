package ownStrategy.logic.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ownStrategy.dto.criteria.FilterDTO;
import ownStrategy.dto.portfolio.PortfolioStrategyDTO;
import ownStrategy.dto.request.RequestDTO;
import ownStrategy.model.entity.criteria.Filter;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;
import ownStrategy.model.entity.portfolio.Request;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface StrategyMapper {
    Request toEntity(RequestDTO dto);
    PortfolioStrategy toEntity(PortfolioStrategyDTO dto);
    PortfolioStrategyDTO toDto(PortfolioStrategy entity);
    Filter toEntity(FilterDTO dto);
    FilterDTO toDto(Filter entity);

    default Optional<PortfolioStrategyDTO> toDtoOptional(Optional<PortfolioStrategy> entity) {
        return entity == null ? null : entity.map(portfolioStrategy -> toDto(portfolioStrategy));
    }

    default Page<PortfolioStrategyDTO> toDtoPage(Page<PortfolioStrategy> entityPage) {
        return entityPage == null ? null : entityPage.map(this::toDto);
    }
    List<PortfolioStrategyDTO> toDtoList(List<PortfolioStrategy> entities);
}