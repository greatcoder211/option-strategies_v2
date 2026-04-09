package ownStrategy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.Update;

import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ownStrategy.dto.ChartPoint;
import ownStrategy.dto.FilterDTO;
import ownStrategy.dto.SortDTO;
import ownStrategy.dto.Status;
import ownStrategy.exceptions.APILimitExceededException;
import ownStrategy.exceptions.OptionTypeException;
import ownStrategy.exceptions.QuantityException;
import ownStrategy.exceptions.SpreadException;
import ownStrategy.legacy.console.Chart;
import ownStrategy.logic.WalletFilter;
import ownStrategy.logic.finance.OptionCalculator;
import ownStrategy.logic.network.AlphaVantageStock;
import ownStrategy.logic.sPattern.*;
import ownStrategy.model.TheWallet;
import ownStrategy.model.User;
import ownStrategy.repository.StrategyRepository;
import ownStrategy.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OptionService {
    private final StrategyRepository repo;
    private final UserRepository userRepo;
    private final MongoTemplate mongoTemplate;
    private final WalletFilter walletFilter;
    public OptionService(StrategyRepository repo, UserRepository userRepo, MongoTemplate mongoTemplate, WalletFilter walletFilter) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.mongoTemplate = mongoTemplate;
        this.walletFilter = walletFilter;
    }
/*    public SpreadStrategy requested(String type, Belfort belfort) {
        String formattedType = Arrays.stream(type.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining("_"));
        try {
            Class<? extends SpreadStrategy> clazz = StrategyType.valueOf(formattedType).getStrategyClass();
            return clazz.getConstructor(String.class, Belfort.class)
                    .newInstance(formattedType.replace("_", " "), belfort);

        } catch (Exception e) {
            throw new RuntimeException("We couldn't manage to initialize your strategy: " + type);
        }
    }*/

    public void checkQuant(int quant){
        if(quant < 1){
            throw new QuantityException();
        }
    }

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
    public void setType(OptionType type, SpreadStrategy strategy) {
        if (type == null) return;
        if(type.equals(OptionType.CALL)){
            strategy.setType(OptionType.CALL);
        }
        else if(type.equals(OptionType.PUT)){
            strategy.setType(OptionType.PUT);
        }
    }
    public List<OptionLeg> calculateLegs(SpreadStrategy os, double price, List<Double> spreadValues) {
        List<Double> prices = os.setThePrices(price, spreadValues, os);
        for (int i = 0; i < prices.size(); i++) {
            prices.set(i, Math.round(prices.get(i) * 100.0) / 100.0);
        }
        os.setLegs(os.setOptionLegs(prices));
        return os.setOptionLegs(prices);
    }

    public double getStockPrice(String ticker) {
        return AlphaVantageStock.getPrice(ticker);
    }

    public List<ChartPoint> chart(OptionStrategy strategy, List<OptionLeg> legs, double price, int quantity) {
        List<ChartPoint> points = new ArrayList<>();
        try {
            points = Chart.draw(strategy, legs, price, quantity);
        } catch (Exception e) { e.printStackTrace();}
        return points;
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
    public void validateSpreads(List<Double> spreads, SpreadStrategy strategy) {
        String message = "Invalid spreads. Only positive numerical values.";
        if (spreads == null || spreads.isEmpty()) {
            throw new SpreadException(message);
        }
        for (Double s : spreads) {
            if (s == null || s <= 0) {
                throw new SpreadException(message);
            }
        }
        //spread number check
        if(strategy.getSpreadNumber() != spreads.size()){
            throw new SpreadException(message);
        }
    }
    public List<TheWallet> getAllTheseWallets(){
        return repo.findAll();
    }

    public void cleanUp(String id) {
        Optional<TheWallet> wallet = repo.findById(id);
        if(wallet.isPresent()){
            repo.delete(wallet.get());
        }
    }

    public Optional<TheWallet> getWallet(String id){
        if(repo.findById(id).isPresent()){
            return repo.findById(id);
        }
        return Optional.empty();
    }

    public void saveToRepo(TheWallet wallet) {
        wallet.setDate(LocalDateTime.now());
        wallet.setStatus(Status.OPEN);
        repo.save(wallet);
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
            return repo.findByUserID(UserId);
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
        return OptionCalculator.function(strategy, currentPrice, gamePrice, quant);
    }

    @Transactional
    public void closePosition() {
        List<TheWallet> wallets = repo.findAll();
        for (TheWallet wallet : wallets) {
            if(wallet.getExpiry().isBefore(LocalDate.now())){
                wallet.setStatus(Status.CLOSED);
                repo.save(wallet);
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
                case "Max price"   -> walletFilter.sortByHighestPrice();
                case "Min price"   -> walletFilter.sortByLowestPrice();
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

    public void checkType(OptionType optionType, SpreadStrategy strategy) {
        String name = strategy.getName();
        if((name.contains("Iron Butterfly") || name.contains("Iron Condor") || name.contains("Strangle")) && !optionType.equals(OptionType.NA))
            throw new OptionTypeException("Wrong optiontype. This type of strategy can only be played in a CALL or PUT variant.");
        else if((name.contains("Butterfly") || name.contains("Ratio Spread") || name.contains("Backspread") || name.contains("Bull") || name.contains("Bear")) && optionType.equals(OptionType.NA))
            throw new OptionTypeException("Wrong optiontype. You have to play CALL or PUT.");
    }

    public void currentPriceCheck(double currentPrice) {
        if(currentPrice == -1)
            throw new APILimitExceededException("Probably API limit exceeded. See you tomorrow!");
    }

}
