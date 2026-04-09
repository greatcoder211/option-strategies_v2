package ownStrategy.exceptions;

public class StrategyNotFoundException extends RuntimeException{
    public StrategyNotFoundException(){
        super("Strategy fot found");
    }
}
