package ownStrategy.model.structure;

import java.time.LocalDate;
import java.util.List;
public interface SpreadStructure {
    List<Double> setPrices(double spotPrice, List<Double> spreads);
}
/*
Wzorzec strategia- ostatecznie zawieszony w ramach tego projektu, z uwagi na brak sensu biznesowego.
Logika przydzielania cen strikeprice w option legach nie jest uniwersalna, a logika z datami byłaby karkołomna(nie warta świeczki).

W bardzo odległej- i wątpliwej przyszłości być może kiedyś okaże się to użyteczne:

Kiedy interfejsy kategoryzujące mają sens biznesowy?
Tego typu abstrakcje warto wprowadzać w architekturze tylko w dwóch konkretnych scenariuszach:

Margin Calculation (Wymogi depozytowe): Systemy brokerskie zupełnie inaczej kalkulują depozyt dla strategii o zdefiniowanym ryzyku wertykalnym, a inaczej dla kalendarzowych czy nagich opcji. Wtedy pusty interfejs (tzw. Marker Interface), np. VerticalStrategy, może posłużyć kalkulatorowi ryzyka do szybkiego skierowania logiki do odpowiedniego algorytmu matematycznego.

        Position Recognition (Rozpoznawanie portfela): Jeśli w przyszłości Twój system miałby analizować "surowy" portfel (np. listę 15 niepowiązanych opcji) i automatycznie parować je w gotowe strategie. Wtedy silnik skanujący mógłby używać takich interfejsów, by odpytywać obiekty: "czy te dwie konkretne opcje spełniają warunek bycia spreadem wertykalnym?".
        */