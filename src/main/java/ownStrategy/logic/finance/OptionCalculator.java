package ownStrategy.logic.finance;

public class OptionCalculator {

    public static double function(OptionStrategy o, double price, double price2, int quantity) {
        return quantity * (o.calculateNetPremium(o.getLegs(), price) + o.calculatePayoff(o.getLegs(), price2));
    }

}
//strikePrice- stockprice w momencie zakupu strategii
//price2- nowa cena(nowy stockprice)
