package utility;

import Authentication.User;
import Utility.AskBreak;
import Utility.StandardConsole;

import java.util.NoSuchElementException;

public class AskAuth {
    private final StandardConsole console;

    public AskAuth(StandardConsole console) {
        this.console = console;
    }

    public User askCredentials(){
        try {
            console.println("Введите логин:");
            console.print("> ");
            String login = console.readln().trim();
            if (login.isEmpty()) {
                console.printError("Логин не может быть пустым");
                return null;
            }

            console.println("Введите пароль:");
            console.print("> ");
            String password = console.readln().trim();
            if (password.isEmpty()) {
                console.printError("Пароль не может быть пустым");
                return null;
            }

            return new User(login, password);
        } catch (NoSuchElementException e) {
            console.printError("Ошибка ввода");
            return null;
        }
    }
}