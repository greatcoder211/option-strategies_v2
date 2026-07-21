package ownStrategy.logic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import org.springframework.data.domain.Page;
import ownStrategy.dto.criteria.FilterDTO;
import ownStrategy.dto.portfolio.CompanyDTO;
import ownStrategy.dto.portfolio.PortfolioStrategyDTO;
import ownStrategy.dto.request.*;
import ownStrategy.model.entity.criteria.Filter;
import ownStrategy.model.entity.portfolio.Company;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;
import ownStrategy.model.entity.request.*;

import java.util.List;
import java.util.Optional;
//sorted by mapping categories in lexical order
@Mapper(componentModel = "spring")
public interface StrategyMapper {

    //dtos
    PortfolioStrategyDTO toDto(PortfolioStrategy entity);
    FilterDTO toDto(Filter entity);

    //entities
    @SubclassMapping(source = ButterflySpreadRequestDTO.class, target = ButterflySpreadRequest.class)
    @SubclassMapping(source = CalendarSpreadRequestDTO.class, target = CalendarSpreadRequest.class)
    @SubclassMapping(source = IronButterflyRequestDTO.class, target = IronButterflyRequest.class)
    @SubclassMapping(source = IronCondorRequestDTO.class, target = IronCondorRequest.class)
    @SubclassMapping(source = PoorMansCoveredRequestDTO.class, target = PoorMansCoveredRequest.class)
    @SubclassMapping(source = RatioSpreadRequestDTO.class, target = RatioSpreadRequest.class)
    @SubclassMapping(source = StrangleRequestDTO.class, target = StrangleRequest.class)
    @SubclassMapping(source = VerticalSpreadRequestDTO.class, target = VerticalSpreadRequest.class)
    Request toEntity(RequestDTO dto);
    PortfolioStrategy toEntity(PortfolioStrategyDTO dto);
    Filter toEntity(FilterDTO dto);

    //lists
    List<PortfolioStrategyDTO> toDtoPortfolioStrategyList(List<PortfolioStrategy> entities);
    List<CompanyDTO> toDtoCompanyList(List<Company> entities);

    //optionals
    default Optional<PortfolioStrategyDTO> toDtoOptional(Optional<PortfolioStrategy> entity) {
        return entity == null ? null : entity.map(portfolioStrategy -> toDto(portfolioStrategy));
    }

    //pages
    default Page<PortfolioStrategyDTO> toDtoPage(Page<PortfolioStrategy> entityPage) {
        return entityPage == null ? null : entityPage.map(this::toDto);
    }
}