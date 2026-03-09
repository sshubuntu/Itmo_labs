package Client;

import Utility.Console;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Консоль для ввода команд и вывода результата
 * @author sh_ub
 */
public class StandardConsole implements Console {
    private static final String P = "> ";
    private static final Scanner defScanner = new Scanner(System.in);

    public void print(Object message) {
        System.out.print(message);
    }

    public void println(Object message) {
        System.out.println(message);
    }

    public void printError(Object obj) {
        System.out.println("Error: " + obj);
    }

    public String readln() throws NoSuchElementException, IllegalStateException {
        return ( defScanner).nextLine();
    }

    public String printTable(String RightEl, String LeftEl) {
        return String.format(" %-35s%-1s%n", RightEl, LeftEl);

    }
    public void prompt() {
        print(P);
    }

    public String getPrompt() {
        return P;
    }

}
