package ownStrategy.logic.finance;

import ownStrategy.logic.sPattern.OptionStrategy;

public class OptionCalculator {

    public static double function(OptionStrategy o, double price, double price2, int quantity) {
        return quantity * (o.netPremium(o.getLegs(), price) + o.calculateProfits(o.getLegs(), price2));
    }

}
//price- stockprice w momencie zakupu strategii
//price2- nowa cena(nowy stockprice)
