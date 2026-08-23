package ownStrategy.model;

import java.time.LocalDate;

//czysty model matematyczny, kurier do przesyłania danych
public record PricingContext(double riskFreeRate,
                             double volatility, LocalDate evaluationDate){}
//0.05
//0.30