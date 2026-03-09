package Gui;

import Authentication.User;
import Connection.ConnectionClient;
import Utility.ExecutionResponse;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class AuthForm {
    private ResourceBundle bundle;
    private Stage stage;
    private ConnectionClient networkClient;

    // UI Components
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Button submitButton;
    private Button switchModeButton;
    private Label titleLabel;
    private Label usernameLabel;
    private Label passwordLabel;
    private Label confirmPasswordLabel;
    private Label errorLabel;
    private ComboBox<String> languageSelector;

    private boolean isLoginMode = true;
    private String currentError = null;

    public AuthForm(ResourceBundle bundle, ConnectionClient networkClient) {
        this.bundle = bundle;
        this.networkClient = networkClient;
        this.stage = new Stage();
        initializeUI();
    }

    private void initializeUI() {
        // Create the auth form layout
        VBox authCard = new VBox(15);
        authCard.setPadding(new Insets(30));
        authCard.setAlignment(Pos.CENTER);
        authCard.setMaxWidth(350);
        authCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);");
        authCard.setEffect(new GaussianBlur(2));

        // Language selector
        languageSelector = new ComboBox<>();
        languageSelector.getItems().addAll("English", "Русский", "Nederlands", "Lietuvių");
        languageSelector.setValue("English");
        languageSelector.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-font-size: 14;");
        languageSelector.setOnAction(e -> updateLanguage());
        HBox langBox = new HBox(languageSelector);
        langBox.setAlignment(Pos.TOP_RIGHT);

        // Initialize UI components
        titleLabel = new Label();
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#1c2526"));

        usernameLabel = new Label();
        usernameLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        usernameLabel.setTextFill(Color.web("#1c2526"));
        usernameField = new TextField();
        usernameField.setPromptText(getResourceString("username", "Username"));
        usernameField.setPrefHeight(44);
        usernameField.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                "-fx-border-color: #d1d1d6; -fx-border-radius: 10; -fx-font-size: 16;");

        passwordLabel = new Label();
        passwordLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        passwordLabel.setTextFill(Color.web("#1c2526"));
        passwordField = new PasswordField();
        passwordField.setPromptText(getResourceString("password", "Password"));
        passwordField.setPrefHeight(44);
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                "-fx-border-color: #d1d1d6; -fx-border-radius: 10; -fx-font-size: 16;");

        confirmPasswordLabel = new Label();
        confirmPasswordLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        confirmPasswordLabel.setTextFill(Color.web("#1c2526"));
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText(getResourceString("confirm_password", "Confirm Password"));
        confirmPasswordField.setPrefHeight(44);
        confirmPasswordField.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                "-fx-border-color: #d1d1d6; -fx-border-radius: 10; -fx-font-size: 16;");

        // Error label
        errorLabel = new Label();
        errorLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        errorLabel.setTextFill(Color.web("#ff3b30"));
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);

        submitButton = new Button();
        submitButton.setPrefHeight(44);
        submitButton.setMaxWidth(200);
        submitButton.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        submitButton.setStyle("-fx-background-color: #007aff; -fx-text-fill: white; -fx-background-radius: 10;");
        submitButton.setOnAction(e -> handleSubmit());
        submitButton.setOnMouseEntered(e -> submitButton.setStyle("-fx-background-color: #005bd4; -fx-text-fill: white; -fx-background-radius: 10;"));
        submitButton.setOnMouseExited(e -> submitButton.setStyle("-fx-background-color: #007aff; -fx-text-fill: white; -fx-background-radius: 10;"));

        switchModeButton = new Button();
        switchModeButton.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        switchModeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #007aff;");
        switchModeButton.setOnAction(e -> switchMode());
        switchModeButton.setOnMouseEntered(e -> switchModeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #005bd4; -fx-underline: true;"));
        switchModeButton.setOnMouseExited(e -> switchModeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #007aff;"));

        authCard.getChildren().addAll(
                langBox,
                titleLabel,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                confirmPasswordLabel,
                confirmPasswordField,
                errorLabel,
                submitButton,
                switchModeButton
        );

        StackPane root = new StackPane(authCard);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f5f7, #e0e0e6);");

        Scene scene = new Scene(root, 400, 600);
        stage.setScene(scene);
        stage.setTitle(getResourceString("app_title", "Collection Client"));
        stage.setResizable(false);

        updateUIText();
        updateMode();
    }

    private void handleSubmit() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || (!isLoginMode && confirmPassword.isEmpty())) {
            showError("empty_fields");
            return;
        }

        if (!isLoginMode && !password.equals(confirmPassword)) {
            showError("passwords_dont_match");
            return;
        }

        try {
            networkClient.start();
            networkClient.send(networkClient.serializeObject(new User(username, password, isLoginMode ? "login" : "registration")));
            ExecutionResponse response = networkClient.deserializeObject(networkClient.receive());

            if (response.isSuccess()) {
                hideError();
                stage.close();
                // Open main window after successful authentication
                MainWindow mainWindow = new MainWindow(bundle, username, new User(username, password));
                mainWindow.show();
            } else {
                showError(response.getResponse());
            }
        } catch (IOException | ClassNotFoundException e) {
            showError("connection_error");
        }
    }

    private void showError(String messageOrKey) {
        String errorMessage;
        switch (messageOrKey) {
            case "empty_fields", "passwords_dont_match", "connection_error"-> errorMessage = getResourceString(messageOrKey, "Error");
            default -> errorMessage = messageOrKey;
        }
        currentError = messageOrKey; // Store the original key/message
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        currentError = null;
        errorLabel.setVisible(false);
    }

    private void switchMode() {
        isLoginMode = !isLoginMode;
        hideError();
        updateMode();
    }

    private void updateMode() {
        confirmPasswordField.setVisible(!isLoginMode);
        confirmPasswordLabel.setVisible(!isLoginMode);
        updateUIText();
    }

    private void updateLanguage() {
        String selected = languageSelector.getValue();
        Locale newLocale;
        switch (selected) {
            case "Русский":
                newLocale = new Locale("ru");
                break;
            case "Nederlands":
                newLocale = new Locale("nl");
                break;
            case "Lietuvių":
                newLocale = new Locale("lt");
                break;
            default:
                newLocale = new Locale("en", "IN");
                break;
        }
        bundle = LocalizationManager.getResourceBundle(newLocale);
        stage.setTitle(getResourceString("app_title", "Collection Client"));
        updateUIText();

        // Update error message if there is one
        if (currentError != null) {
            errorLabel.setText(getResourceString(currentError, "Error"));
        }
    }

    private String getResourceString(String key, String fallback) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return fallback;
        }
    }

    private void updateUIText() {
        titleLabel.setText(getResourceString(isLoginMode ? "login_title" : "registration_title",
                isLoginMode ? "Sign In" : "Registration"));
        usernameLabel.setText(getResourceString("username", "Username"));
        passwordLabel.setText(getResourceString("password", "Password"));
        confirmPasswordLabel.setText(getResourceString("confirm_password", "Confirm Password"));
        submitButton.setText(getResourceString(isLoginMode ? "login" : "register",
                isLoginMode ? "Sign In" : "Register"));
        switchModeButton.setText(getResourceString(isLoginMode ? "switch_to_register" : "switch_to_login",
                isLoginMode ? "Create Account" : "Back to Login"));
        usernameField.setPromptText(getResourceString("username", "Username"));
        passwordField.setPromptText(getResourceString("password", "Password"));
        confirmPasswordField.setPromptText(getResourceString("confirm_password", "Confirm Password"));
    }

    public void show() {
        stage.show();
    }
} 