package ownStrategy.model;
//czysty model matematyczny, kurier do przesyłania danych
public record PricingContext(double riskFreeRate,
                            double volatility){}
//0.05
//0.30