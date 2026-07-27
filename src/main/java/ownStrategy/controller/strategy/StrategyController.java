package ownStrategy.controller.strategy;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ownStrategy.dto.criteria.FilterDTO;
import ownStrategy.dto.portfolio.CompanyDTO;
import ownStrategy.dto.portfolio.PortfolioStrategyDTO;
import ownStrategy.dto.request.RequestDTO;
import ownStrategy.logic.mapper.StrategyMapper;
import ownStrategy.model.entity.portfolio.ChartPoint;
import ownStrategy.model.entity.criteria.Filter;
import ownStrategy.model.entity.portfolio.PortfolioStrategy;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.service.strategy.StrategyBuilderService;
import ownStrategy.service.strategy.StrategyFilterService;
import ownStrategy.service.strategy.StrategyService;

import java.util.List;
import java.util.Optional;
//dotyczy jednej strategii lub całego portfolio składającego się z wielu strategii
@RestController
@CrossOrigin(origins = "*")
public class StrategyController {
    private final StrategyService strategyService;
    private final StrategyBuilderService strategyBuilderService;
    private final StrategyFilterService strategyFilterService;
    private final StrategyMapper strategyMapper;
    private final OptionStrategyModelAssembler optionStrategyModelAssembler;

    public StrategyController(StrategyService strategyService, StrategyBuilderService strategyBuilderService,
                              StrategyFilterService strategyFilterService, StrategyMapper strategyMapper, OptionStrategyModelAssembler optionStrategyModelAssembler) {
        this.strategyService = strategyService;
        this.strategyBuilderService = strategyBuilderService;
        this.strategyFilterService = strategyFilterService;
        this.strategyMapper = strategyMapper;
        this.optionStrategyModelAssembler = optionStrategyModelAssembler;
    }

/*  TODO: gucknij, albo do wywalenia, albo do przerobienia(czy chcę w ogóle linki na poziomie już zagranych strategii z portfolio?
    @PostMapping
    public ResponseEntity<EntityModel<PortfolioStrategy>> createStrategy(@RequestBody RequestDTO request) {
        PortfolioStrategy portfolioStrategy = strategyBuilderService.createStrategy(strategyMapper.toEntity(request));
        EntityModel<PortfolioStrategy> model = optionStrategyModelAssembler.toModel(portfolioStrategy);
        return ResponseEntity.ok(model);
    }*/

    //tutaj przykład: dto -> oryginalna encja -> przekazujemy dalej -> dto na powrót
    @PostMapping("/execute")
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioStrategyDTO executeStrategy(@RequestBody RequestDTO requestDto){
        return strategyMapper.toDto(strategyBuilderService.createStrategy(strategyMapper.toEntity(requestDto)));
    }

    @GetMapping("/companies/{keySearch}")
    public List<CompanyDTO> listOfCompanies(@PathVariable String keySearch){
        return strategyMapper.toDtoCompanyList(strategyBuilderService.generateListOfCompanies(keySearch));
    }

    //EntityModel<> jest niezbędny do tego, żeby dokleił mi linki HATEOAS
    @PostMapping("/preview")
    public EntityModel<StrategyContracts.PreviewChartResponse> previewStrategy(@RequestBody RequestDTO requestDto){
        //argument obronny ze jest tu trochę logiki- nie da rady inaczej przez limity API, a ceny jest tkanką wspolną tego, co jest wymagane w serwisie i w assemblerze(controller to spaja)
//        double spotPrice = strategyBuilderService.getSpotPrice(requestDto.getSelectedCompany().ticker());
        double spotPrice = 220.0;
        EntityModel<OptionStrategy> modelAssembler = optionStrategyModelAssembler.toModel(strategyBuilderService.mapRequestToOptionStrategy(strategyMapper.toEntity(requestDto), spotPrice));
        return EntityModel.of(new StrategyContracts.PreviewChartResponse(requestDto.getStrategyName(), strategyBuilderService.processPreviewChart(strategyMapper.toEntity(requestDto), spotPrice)), modelAssembler.getLinks());
    }

    //dto -> original -> service -> return double
    @PostMapping("/portfolio/score")
    public double checkPnL(@RequestBody PortfolioStrategyDTO portfolioStrategyDto){
        PortfolioStrategy portfolioStrategy = strategyMapper.toEntity(portfolioStrategyDto);
        //nie da się pominąć serwisu, bo trzeba skorzystać z MarketClienta
        return strategyService.calculatePnL(portfolioStrategy.getOptionLegs(), portfolioStrategy.getSpotPrice(), portfolioStrategy.getCompany().ticker());
    }

    //dto -> original -> create dto -> return dto
    @PostMapping("/portfolio/chart")
    //po prostu wykres strategii wraz z punktem, w którym jesteśmy
    public StrategyContracts.ScoreChartResponse scoreChart(@RequestBody PortfolioStrategyDTO portfolioStrategyDto){
        PortfolioStrategy portfolioStrategy = strategyMapper.toEntity(portfolioStrategyDto);
        List<ChartPoint> strategyChart = strategyService.makeChart(portfolioStrategy.getOptionLegs(), portfolioStrategy.getSpotPrice());
        ChartPoint currentPriceMarker = strategyService.makeCurrentPriceMarker(portfolioStrategy.getOptionLegs(), portfolioStrategy.getSpotPrice(), portfolioStrategy.getCompany().ticker());
        return new StrategyContracts.ScoreChartResponse(strategyChart, currentPriceMarker);
    }

    @GetMapping("/portfolio/filter")
    public Page<PortfolioStrategyDTO> getFilteredTrades(FilterDTO filterDto) {
        Filter filter = strategyMapper.toEntity(filterDto);
        return strategyMapper.toDtoPage(strategyFilterService.filter(filter));
    }

    @GetMapping("/portfolio/get/all")
    public List<PortfolioStrategyDTO> getAllPortfolios(){
        return strategyMapper.toDtoPortfolioStrategyList(strategyService.getAllPortfolios());
    }

    @GetMapping("/strategy/get/id/{strategyId}")
    public Optional<PortfolioStrategyDTO> getStrategyByStrategyId(@PathVariable String strategyId){
        return strategyMapper.toDtoOptional(strategyService.getStrategyByStrategyId(strategyId));
    }

    @GetMapping("/portfolio/get/username/{username}")
    public List<PortfolioStrategyDTO> getPortfolioByUsername(@PathVariable String username){
        return strategyMapper.toDtoPortfolioStrategyList(strategyService.getPortfolioByUsername(username));
    }

    @GetMapping("/alphavantage/limit/check")
    public ResponseEntity<Void> alphavantageLimitExceededCheck(String companyTicker) {
        strategyBuilderService.getSpotPrice(companyTicker);
        return ResponseEntity.ok().build();
    }
}