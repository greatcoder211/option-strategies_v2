package ownStrategy.legacy.console;

import ownStrategy.logic.OptionRepositoryL;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        OptionUIL ui = new OptionUIL(sc);
        OptionServiceL serv = new OptionServiceL();
        OptionRepositoryL repo = new OptionRepositoryL();

        OptionControllerL controller = new OptionControllerL(serv, repo, ui);

        controller.start();
    }
}
//chyba za duzo zapytan wyslalem i jest za duzo zapytan- jutra dalszy debug
//spring, zrobic controller, automatyczne odswiezanie/
//np data driven
//JSON do NoSQL(do bazy danych)
