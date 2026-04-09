package ownStrategy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ownStrategy.dto.CompanyDTO;
import ownStrategy.logic.sPattern.SpreadStrategy;
import ownStrategy.model.TheWallet;
import ownStrategy.model.User;
import ownStrategy.service.MongoDBService;
import ownStrategy.service.OptionService2;
import ownStrategy.logic.network.TickerSearch;

import java.time.LocalDate;
import java.util.List;

@RestController
public class OptionController2 {
    private final TickerSearch tickerSearch;
    private final OptionService2 service;
    private final MongoDBService service2;

    public OptionController2(TickerSearch tickerSearch, OptionService2 service, MongoDBService service2) {
        this.tickerSearch = tickerSearch;
        this.service = service;
        this.service2 = service2;
    }
    @GetMapping("/companies")
    public List<CompanyDTO> getCompanies(@RequestParam String key) {
        return tickerSearch.Companies(key);
    }
    @PostMapping("/portfolio")
    public TheWallet wallet(@RequestParam String userID,
                                      @RequestParam String key,
                                      @RequestParam int choice,
                                      @RequestParam LocalDate expiry,
                                      @RequestBody SpreadStrategy strategy){
        try{
            strategy.setName();
            CompanyDTO company = tickerSearch.Companies(key).get(choice);
            service.NAtype(strategy);
            TheWallet wallet = new TheWallet(company.getTicker(), strategy.getName(), strategy.getType(), strategy.getLegs(), expiry);
            strategy.setPrice(service.giveMePrice(key));
            wallet.setLegs(strategy.setOptionLegs(strategy.setThePrices(strategy.getPrice(), strategy.getSpreads(), strategy)));
            service.firstWalletCheck(wallet, strategy);
            service.checkUser(userID, wallet);
            service.saveToRepo(wallet);
            return wallet;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                    "Sabotage successful: Wallet creation aborted due to error. You have to have a user for the wallet you create."
            );
        }
    }
    @GetMapping("/portfolio")
    public List<TheWallet> allSaved(){
        return service.getAllTheseWallets();
    }
    @GetMapping("/portfolio/hehe/{id}")
    public TheWallet getWallet(@PathVariable String id){
        return service.getWallet(id).get();
    }
    @DeleteMapping("/portfolio/{id}")
    public void delete(
            @PathVariable String id){
        service.cleanUp(id);
    }
    @PostMapping("/users")
    public String addUser(@RequestBody User user){
        service.saveUser(user);
        return user.getId() + " succesfully saved";
    }
    @GetMapping("/portfolio/user/{userID}")
    public List<TheWallet> getUserWallets(@PathVariable String userID){
        try{
            return service.getTheWallets(userID);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                    "I'm the maker. I'm the pusher- but no user"
            );
        }
    }
    @GetMapping("/mongodb/risky/{companyTick}")
    public List<TheWallet> risky(@PathVariable String companyTick){
        return service2.getTopRiskTrades(companyTick);
    }

    @GetMapping("/users/getall")
    public List<User> getAllUsers(){
        return service.getAllUsers();
    }
    @GetMapping("/mongodb/train/{ticker}")
    public List<TheWallet> ofThisTicker(@PathVariable String ticker){
        return service2.train2(ticker);
    }
    @GetMapping("/mongodb/rentier")
    public List<org.bson.Document> Rentier(){
        return service2.rentier();
    }
}
