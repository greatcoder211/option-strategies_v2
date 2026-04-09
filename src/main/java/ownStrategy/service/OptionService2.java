package ownStrategy.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ownStrategy.logic.network.AlphaVantageStock;
import ownStrategy.logic.sPattern.OptionType;
import ownStrategy.logic.sPattern.SpreadStrategy;
import ownStrategy.model.TheWallet;
import ownStrategy.model.User;
import ownStrategy.repository.StrategyRepository;
import ownStrategy.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OptionService2 {
    private final StrategyRepository repo;
    private final UserRepository userRepo;
    public OptionService2(StrategyRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }
    public List<TheWallet> getAllTheseWallets(){
        return repo.findAll();
    }
    public double giveMePrice(String key){
        return AlphaVantageStock.getPrice(key);
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
        if(repo.findById(UserId).isPresent()){
            return repo.findByUserID(UserId);
        }
        else
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + UserId);
    }

    public void firstWalletCheck(TheWallet wallet, SpreadStrategy strategy) {
        if(wallet.getLegs().size()/2 != strategy.getSpreadNumber())
            throw new  ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong spreads entered. Only the number allowed");
        else if(wallet.getType() != OptionType.NA && !strategy.getCP())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong optiontype. For this type of strategy you can only enter 'NA' Non-Applicable");
        else if(wallet.getType() == OptionType.NA && strategy.getCP())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NA doesn't apply here. You have to choose a CALL or PUT variant for this type of strategy.");
    }

    public void NAtype(SpreadStrategy strategy) {
        if(strategy.getType() == null)
            strategy.setType(OptionType.NA);
    }
}
