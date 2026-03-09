package Gui;

import Connection.ConnectionClient;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Главный класс приложения, который служит точкой входа
 */
public class Main extends Application {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1234;
    private ConnectionClient networkClient;

    @Override
    public void start(Stage primaryStage) {
        // Инициализация сетевого клиента
        networkClient = new ConnectionClient(SERVER_PORT, SERVER_HOST);
        
        // Запуск формы аутентификации
        AuthForm authForm = new AuthForm(
            getResourceBundle(),
            networkClient
        );
        authForm.show();
    }

    /**
     * Получение ресурсного бандла для локализации
     */
    private ResourceBundle getResourceBundle() {
        return LocalizationManager.getResourceBundle(new Locale("en", "IN"));
    }

    /**
     * Точка входа в приложение
     */
    public static void main(String[] args) {
        launch(args);
    }
} 