package Gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Route;
import model.Coordinates;
import model.Location;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

public class RouteDialog {
    private Stage stage;
    private Route route;
    private ResourceBundle bundle;
    private boolean confirmed = false;

    // Form fields
    private TextField nameField;
    private TextField coordXField;
    private TextField coordYField;
    private TextField fromXField;
    private TextField fromYField;
    private TextField fromZField;
    private TextField fromNameField;
    private TextField toXField;
    private TextField toYField;
    private TextField toZField;
    private TextField toNameField;
    private TextField distanceField;

    public RouteDialog(ResourceBundle bundle, Route existingRoute) {
        this.bundle = bundle;
        this.route = existingRoute != null ? existingRoute : new Route();
        initializeUI();
    }

    private void initializeUI() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(getResourceString("route_dialog_title", "Route Details"));

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f7;");

        // Route name
        Label nameLabel = new Label(getResourceString("route_name", "Route Name:"));
        nameField = new TextField(route.getName());
        nameField.setPromptText(getResourceString("route_name_prompt", "Enter route name"));

        // Coordinates
        Label coordLabel = new Label(getResourceString("coordinates", "Coordinates:"));
        HBox coordBox = new HBox(10);
        coordXField = new TextField(route.getCoordinates() != null ? route.getCoordinates().getX().toString() : "");
        coordYField = new TextField(route.getCoordinates() != null ? route.getCoordinates().getY().toString() : "");
        coordXField.setPromptText("X");
        coordYField.setPromptText("Y");
        coordBox.getChildren().addAll(coordXField, coordYField);

        // From Location
        Label fromLabel = new Label(getResourceString("from_location", "From Location:"));
        GridPane fromGrid = new GridPane();
        fromGrid.setHgap(10);
        fromGrid.setVgap(5);
        fromXField = new TextField(route.getFrom() != null ? route.getFrom().getX().toString() : "");
        fromYField = new TextField(route.getFrom() != null ? String.valueOf(route.getFrom().getY()) : "");
        fromZField = new TextField(route.getFrom() != null ? route.getFrom().getZ().toString() : "");
        fromNameField = new TextField(route.getFrom() != null ? route.getFrom().getName() : "");
        fromGrid.add(new Label("X:"), 0, 0);
        fromGrid.add(fromXField, 1, 0);
        fromGrid.add(new Label("Y:"), 0, 1);
        fromGrid.add(fromYField, 1, 1);
        fromGrid.add(new Label("Z:"), 0, 2);
        fromGrid.add(fromZField, 1, 2);
        fromGrid.add(new Label(getResourceString("location_name", "Name:")), 0, 3);
        fromGrid.add(fromNameField, 1, 3);

        // To Location
        Label toLabel = new Label(getResourceString("to_location", "To Location:"));
        GridPane toGrid = new GridPane();
        toGrid.setHgap(10);
        toGrid.setVgap(5);
        toXField = new TextField(route.getTo() != null ? route.getTo().getX().toString() : "");
        toYField = new TextField(route.getTo() != null ? String.valueOf(route.getTo().getY()) : "");
        toZField = new TextField(route.getTo() != null ? route.getTo().getZ().toString() : "");
        toNameField = new TextField(route.getTo() != null ? route.getTo().getName() : "");
        toGrid.add(new Label("X:"), 0, 0);
        toGrid.add(toXField, 1, 0);
        toGrid.add(new Label("Y:"), 0, 1);
        toGrid.add(toYField, 1, 1);
        toGrid.add(new Label("Z:"), 0, 2);
        toGrid.add(toZField, 1, 2);
        toGrid.add(new Label(getResourceString("location_name", "Name:")), 0, 3);
        toGrid.add(toNameField, 1, 3);

        // Distance
        Label distanceLabel = new Label(getResourceString("distance", "Distance:"));
        distanceField = new TextField(route.getDistance() > 0 ? String.valueOf(route.getDistance()) : "");
        distanceField.setPromptText(getResourceString("distance_prompt", "Enter distance > 1"));

        // Buttons
        HBox buttonBox = new HBox(10);
        Button saveButton = new Button(getResourceString("save", "Save"));
        Button cancelButton = new Button(getResourceString("cancel", "Cancel"));
        saveButton.setStyle("-fx-background-color: #007aff; -fx-text-fill: white;");
        cancelButton.setStyle("-fx-background-color: #ff3b30; -fx-text-fill: white;");
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> stage.close());
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        root.getChildren().addAll(
            nameLabel, nameField,
            coordLabel, coordBox,
            fromLabel, fromGrid,
            toLabel, toGrid,
            distanceLabel, distanceField,
            buttonBox
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void handleSave() {
        try {
            // Create Coordinates
            Double x = Double.parseDouble(coordXField.getText());
            Float y = Float.parseFloat(coordYField.getText());
            Coordinates coordinates = new Coordinates(x, y);

            // Create From Location
            Long fromX = Long.parseLong(fromXField.getText());
            long fromY = Long.parseLong(fromYField.getText());
            Double fromZ = Double.parseDouble(fromZField.getText());
            String fromName = fromNameField.getText();
            Location from = new Location(fromX, fromY, fromZ, fromName);

            // Create To Location
            Long toX = Long.parseLong(toXField.getText());
            long toY = Long.parseLong(toYField.getText());
            Double toZ = Double.parseDouble(toZField.getText());
            String toName = toNameField.getText();
            Location to = new Location(toX, toY, toZ, toName);

            // Set Route properties
            route.setName(nameField.getText());
            route.setCoordinates(coordinates);
            route.setFrom(from);
            route.setTo(to);
            route.setDistance(Integer.parseInt(distanceField.getText()));
            route.setCreationDate(ZonedDateTime.now());

            confirmed = true;
            stage.close();

        } catch (NumberFormatException e) {
            showError(getResourceString("number_format_error", "Please enter valid numbers"));
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(getResourceString("error", "Error"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getResourceString(String key, String fallback) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return fallback;
        }
    }

    public Route getRoute() {
        return confirmed ? route : null;
    }

    public void show() {
        stage.showAndWait();
    }
} 