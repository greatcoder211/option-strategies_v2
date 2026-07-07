package ownStrategy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ownStrategy.dto.*;
import ownStrategy.dto.strategyPanel.Trade;
import ownStrategy.exception.APILimitExceededException;
import ownStrategy.logic.finance.ChartGenerator;
import ownStrategy.logic.WalletFilter;
import ownStrategy.logic.finance.OptionCalculator;
import ownStrategy.logic.network.client.AlphaVantageStock;
import ownStrategy.logic.oldStrategy.*;
import ownStrategy.model.Belfort;
import ownStrategy.model.TheWallet;
import ownStrategy.model.User;
import ownStrategy.model.strategy.OptionStrategy;
import ownStrategy.repository.StrategyRepository;
import ownStrategy.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OptionService {
    private final StrategyRepository strategyRepo;
    private final UserRepository userRepo;
    private final MongoTemplate mongoTemplate;
    private final WalletFilter walletFilter;

    //---GREAT REFACTORIZATION---
    private final OptionCalculator optionCalculator;
    private final ChartGenerator chartGenerator;

    public OptionService(StrategyRepository strategyRepo, UserRepository userRepo, MongoTemplate mongoTemplate, WalletFilter walletFilter
                        OptionCalculator optionCalculator, ChartGenerator chartGenerator) {
        this.strategyRepo = strategyRepo;
        this.userRepo = userRepo;
        this.mongoTemplate = mongoTemplate;
        this.walletFilter = walletFilter;
        this.optionCalculator = optionCalculator;
        this.chartGenerator = chartGenerator;
    }
//-- BIG THINGS OUGHT TO HAPPEN --

    public List<ChartPoint> calculatePreviewChart(double spotPrice, OptionStrategy strategy) {
        List<ChartPoint> points = new ArrayList<>();
        try {
            points = chartGenerator.draw(spotPrice, strategy);
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
        return points;
    }
    public double getStockPrice(String ticker) {
        return AlphaVantageStock.getPrice(ticker);
    }

// --- DEBUG ---



    public Belfort belfort(String pos){
        if(pos.equals("BUY")){
            return Belfort.BUY;
        }
        else if(pos.equals("SELL")){
            return Belfort.SELL;
        }
        else {
            throw new RuntimeException("You either BUY or SELL. You cannot do it other way." + pos);
        }
    }

    public int getChoice(String line){
        try {
            if(line.charAt(0) <= '9'){
                return Character.getNumericValue(line.charAt(0));
            }
            else{
                return Integer.parseInt(line.split(" ")[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not parse index from: " + line);
        }
    }
    public List<TheWallet> getAllTheseWallets(){
        return strategyRepo.findAll();
    }

    public void cleanUp(String id) {
        Optional<TheWallet> wallet = strategyRepo.findById(id);
        if(wallet.isPresent()){
            strategyRepo.delete(wallet.get());
        }
    }

    public Optional<TheWallet> getWallet(String id){
        if(strategyRepo.findById(id).isPresent()){
            return strategyRepo.findById(id);
        }
        return Optional.empty();
    }

    public void saveToRepo(TheWallet wallet) {
        wallet.setDate(LocalDateTime.now());
        wallet.setStatus(Status.OPEN);
        strategyRepo.save(wallet);
    }

    public void checkUser(String UserId, TheWallet wallet) {
        if(userRepo.findById(UserId).isPresent()){
            wallet.setUserID(UserId);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + UserId);
        }
    }

    public void saveUser(User user) {
        if(!user.getEmail().contains("@")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email must contain '@'");
        }
        else if(!user.getEmail().toLowerCase().equals(user.getEmail())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email cannot contain capital letters");
        }
        userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public List<TheWallet> getTheWallets(String UserId) {
        if(userRepo.findById(UserId).isPresent()){
            return strategyRepo.findByUserID(UserId);
        }
        else
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + UserId);
    }

    public void firstWalletCheck(TheWallet wallet, SpreadStrategy strategy) {
        if(strategy.getType() == null)
            strategy.setType(OptionType.NA);
        if(wallet.getType() == null && !strategy.getCP())
            wallet.setType(OptionType.NA);
        if(wallet.getLegs().size() != strategy.getLegs().size())
            throw new  ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong spreads entered. Only the number allowed");
        else if(wallet.getType() != OptionType.NA && !strategy.getCP())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong optiontype. For this type of strategy you can only enter 'NA' Non-Applicable");
        else if(wallet.getType() == OptionType.NA && strategy.getCP())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NA doesn't apply here. You have to choose a CALL or PUT variant for this type of strategy.");
    }

    public double revenues(OptionStrategy strategy, double currentPrice, double gamePrice, int quant) {
        return optionCalculator.function(gamePrice, currentPrice, strategy);
    }

    @Transactional
    public void closePosition() {
        List<TheWallet> wallets = strategyRepo.findAll();
        for (TheWallet wallet : wallets) {
            if(wallet.getExpiry().isBefore(LocalDate.now())){
                wallet.setStatus(Status.CLOSED);
                strategyRepo.save(wallet);
            }
        }
    }


    public Page<TheWallet> filter(FilterDTO filterDto) {
        List<Criteria> criteriaList = new ArrayList<>();

        // 1. Dodawanie poszczególnych kryteriów, jeśli nie są nullem w DTO
        if (filterDto.getStrategies() != null && !filterDto.getStrategies().isEmpty()) {
            criteriaList.add(walletFilter.filterByStrategies(filterDto.getStrategies()));
        }
        if (filterDto.getSpreadType() != null) {
            criteriaList.add(walletFilter.filterBySpreadType(filterDto.getSpreadType()));
        }
        if (filterDto.getPosition() != null) {
            criteriaList.add(walletFilter.filterByPosition(filterDto.getPosition()));
        }
        if (filterDto.getCompaniesConcat() != null && !filterDto.getCompaniesConcat().isEmpty()) {
            criteriaList.add(walletFilter.filterByCompanies(filterDto.getCompaniesConcat()));
        }
        if (filterDto.getStatus() != null) {
            criteriaList.add(walletFilter.filterByStatus(filterDto.getStatus()));
        }
        if (filterDto.getExpiryFrom() != null || filterDto.getExpiryTo() != null) {
            criteriaList.add(walletFilter.filterByExpiryRange(filterDto.getExpiryFrom(), filterDto.getExpiryTo()));
        }

        // 2. Składanie zapytania
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
            // // Łączy wszystkie aktywne filtry operatorem AND
        }

        // 3. Obsługa sortowania (zawsze z domyślnym wynikiem)
        Sort sort = sort(filterDto.getSortBy());
        Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getSize(), sort);

        query.with(pageable);//sort wywaliłem, bo pageable już go zawiera
        List<TheWallet> list = mongoTemplate.find(query, TheWallet.class);

        return PageableExecutionUtils.getPage(list, pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), TheWallet.class));
        // // Wykonuje finalne zapytanie do bazy MongoDB
    }

    private Sort sort(List<SortDTO> sortBy) {
        // 1. Jeśli lista jest pusta, zwracamy domyślne sortowanie
        if (sortBy == null || sortBy.isEmpty()) {
            return walletFilter.sortByLatestTradeDate();
        }

        Sort finalSort = null;

        // 2. Iterujemy po liście i wyciągamy Stringa do switcha
        for (SortDTO dto : sortBy) {
            String key = dto.getField() + "_" + dto.getDirection();
            Sort currentSort = switch (key) {
                case "Earliest date" -> walletFilter.sortByEarliestTradeDate();
                case "Max quantity"   -> walletFilter.sortByHighestQuantity();
                case "Min quantity"   -> walletFilter.sortByLowestQuantity();
                case "Max strikePrice"   -> walletFilter.sortByHighestPrice();
                case "Min strikePrice"   -> walletFilter.sortByLowestPrice();
                case "Earliest expiry date" -> walletFilter.sortByFastestExpiry();
                case "Latest expiry date" -> walletFilter.sortByLatestExpiry();
                default           -> walletFilter.sortByLatestTradeDate();
            };
            if (finalSort == null) {
                finalSort = currentSort;
            } else {
                finalSort = finalSort.and(currentSort);
            }
        }

        return finalSort;
    }

    public void currentPriceCheck(double currentPrice) {
        if(currentPrice == -1)
            throw new APILimitExceededException("Probably API limit exceeded. See you tomorrow!");
    }

}
/*    public SpreadStrategy requested(String type, Belfort position) {
        String formattedType = Arrays.stream(type.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining("_"));
        try {
            Class<? extends SpreadStrategy> clazz = StrategyType.valueOf(formattedType).getStrategyClass();
            return clazz.getConstructor(String.class, Belfort.class)
                    .newInstance(formattedType.replace("_", " "), position);

        } catch (Exception e) {
            throw new RuntimeException("We couldn't manage to initialize your strategy: " + type);
        }
    }*/
