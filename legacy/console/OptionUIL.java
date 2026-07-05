package ownStrategy.legacy.console;

import ownStrategy.logic.network.TickerSearch;

import java.util.Scanner;

public class OptionUIL {
    private final Scanner sc;

    public OptionUIL(Scanner sc) {
        this.sc = sc;
    }

    public int getInt() {
        while (!sc.hasNextInt()) {
            System.out.println("Enter the right value!");
            sc.next();
        }
        return sc.nextInt();
    }

    public double getDouble() {
        while (!sc.hasNextDouble()) {
            System.out.println("Enter the right value!");
            sc.next();
        }
        return sc.nextDouble();
    }

    public String getNextLine(String message) {
        if (!message.isEmpty()) System.out.println(message);
        return sc.nextLine();
    }

    public String getTicker() {
        return TickerSearch.Ticker(sc);
    }

    public void print(String message) {
        System.out.print(message);
    }

    public void print2(String message) {
        System.out.println(message);
    }
}