package Gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.canvas.Canvas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.converter.IntegerStringConverter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;
import model.Route;
import model.Coordinates;
import model.Location;
import Authentication.User;
import Utility.ExecutionResponse;
import java.util.List;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import javafx.beans.property.DoubleProperty;
import javafx.scene.canvas.GraphicsContext;

public class MainWindow {
    private final Stage stage;
    private ResourceBundle bundle;
    private Label currentUserLabel;
    private TableView<Route> collectionTable;
    private final String currentUsername;
    private final Map<String, Button> commandButtons;
    private Label tableTitle;
    private Label visualizationTitle;
    private final ObservableList<Route> routes;
    private final NetworkManager networkManager;
    private final User currentUser;
    private Button searchButton;
    private Button clearButton;
    private Canvas visualizationCanvas;
    private final DoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);
    private final DoubleProperty offsetXProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty offsetYProperty = new SimpleDoubleProperty(0);
    private double dragStartX, dragStartY;
    private boolean dragging = false;
    private Route selectedRoute = null;

    public MainWindow(ResourceBundle bundle, String username, User user) {
        this.bundle = bundle;
        this.currentUsername = username;
        this.stage = new Stage();
        this.commandButtons = new HashMap<>();
        this.routes = FXCollections.observableArrayList();
        this.networkManager = new NetworkManager(bundle);
        this.networkManager.setCurrentUser(user);
        this.currentUser = user;
        initializeUI();
        loadRoutes();
        collectionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                animateFocusOnRoute(newSel);
            }
        });
    }

    private void initializeUI() {
        // Create main layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f7;");

        // Top bar with user info and controls
        HBox topBar = createTopBar();
        
        // Info button in top right corner
        StackPane infoButtonContainer = new StackPane();
        infoButtonContainer.setAlignment(Pos.TOP_RIGHT);
        infoButtonContainer.setPadding(new Insets(0, 0, 10, 0));
        
        Button infoButton = new Button("i");
        infoButton.setFont(Font.font("System", FontWeight.BOLD, 16));
        infoButton.setStyle(
            "-fx-background-color: #2c3e50;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 35px;" +
            "-fx-min-height: 35px;" +
            "-fx-max-width: 35px;" +
            "-fx-max-height: 35px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
            "-fx-cursor: hand;"
        );
        infoButton.setOnAction(e -> handleCommand("info"));
        
        // Add hover effect
        infoButton.setOnMouseEntered(e -> 
            infoButton.setStyle(
                "-fx-background-color: #34495e;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 50%;" +
                "-fx-min-width: 35px;" +
                "-fx-min-height: 35px;" +
                "-fx-max-width: 35px;" +
                "-fx-max-height: 35px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 3);" +
                "-fx-cursor: hand;"
            )
        );
        infoButton.setOnMouseExited(e -> 
            infoButton.setStyle(
                "-fx-background-color: #2c3e50;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 50%;" +
                "-fx-min-width: 35px;" +
                "-fx-min-height: 35px;" +
                "-fx-max-width: 35px;" +
                "-fx-max-height: 35px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);" +
                "-fx-cursor: hand;"
            )
        );
        
        infoButtonContainer.getChildren().add(infoButton);
        
        // Split pane for table and visualization
        SplitPane mainContent = new SplitPane();
        mainContent.setDividerPositions(0.4);

        // Collection table
        VBox tableContainer = createTableContainer();
        
        // Visualization area
        VBox visualizationContainer = createVisualizationContainer();

        mainContent.getItems().addAll(tableContainer, visualizationContainer);

        root.getChildren().addAll(infoButtonContainer, topBar, mainContent);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle(getResourceString("app_title", "Collection Client"));
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Profile photo with tooltip
        Image profileImage = new Image(getClass().getResourceAsStream("/images/profile.png"));
        ImageView profileView = new ImageView(profileImage);
        profileView.setFitWidth(45);
        profileView.setFitHeight(32);
        profileView.setPreserveRatio(true);
        profileView.setSmooth(true);
        profileView.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 1);");

        Tooltip userTooltip = new Tooltip();
        userTooltip.setStyle(
            "-fx-background-color: #2c3e50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px;" +
            "-fx-background-radius: 5px;"
        );
        String userInfo = String.format("Username: %s", currentUsername);
        userTooltip.setText(userInfo);
        profileView.setOnMouseEntered(e -> {
            profileView.setOpacity(0.8);
            userTooltip.show(profileView, e.getScreenX(), e.getScreenY() + 10);
        });
        profileView.setOnMouseExited(e -> {
            profileView.setOpacity(1.0);
            userTooltip.hide();
        });

        // Language selector
        ComboBox<String> languageSelector = new ComboBox<>();
        languageSelector.getItems().addAll("English", "Русский", "Nederlands", "Lietuvių");
        languageSelector.setValue("English");
        languageSelector.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-font-size: 14;");
        languageSelector.setOnAction(e -> updateLanguage(languageSelector.getValue()));

        // Command buttons
        Button addButton = createCommandButton("add", "Add Object");
        Button removeButton = createCommandButton("remove", "Remove Object");
        Button clearButton = createCommandButton("clear", "Clear Collection");

        // Store buttons in map for later updates
        commandButtons.put("add", addButton);
        commandButtons.put("remove", removeButton);
        commandButtons.put("clear", clearButton);

        topBar.getChildren().addAll(profileView, languageSelector, addButton, removeButton, clearButton);
        return topBar;
    }

    private VBox createTableContainer() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        tableTitle = new Label(getResourceString("collection_table", "Collection"));
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Add search field
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label searchLabel = new Label(getResourceString("search", "Search:"));
        TextField searchField = new TextField();
        searchField.setPromptText(getResourceString("search_prompt", "Enter route name..."));
        searchField.setPrefWidth(200);
        
        searchButton = new Button(getResourceString("search_button", "Search"));
        searchButton.setStyle("-fx-background-color: #007aff; -fx-text-fill: white; -fx-background-radius: 5;");
        
        clearButton = new Button(getResourceString("clear_button", "Clear"));
        clearButton.setStyle("-fx-background-color: #ff3b30; -fx-text-fill: white; -fx-background-radius: 5;");
        
        searchBox.getChildren().addAll(searchLabel, searchField, searchButton, clearButton);

        collectionTable = new TableView<>();
        collectionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        collectionTable.setItems(routes);
        
        // Add columns
        addTableColumns();

        container.getChildren().addAll(tableTitle, searchBox, collectionTable);
        
        // Add search functionality
        searchButton.setOnAction(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                filterRoutesByName(searchText);
            }
        });
        
        clearButton.setOnAction(e -> {
            searchField.clear();
            loadRoutes(); // Reload all routes
        });
        
        // Add search on Enter key
        searchField.setOnAction(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                filterRoutesByName(searchText);
            }
        });

        return container;
    }

    private void addTableColumns() {
        TableColumn<Route, Long> idColumn = new TableColumn<>(getResourceString("id", "ID"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setEditable(false);

        TableColumn<Route, String> nameColumn = new TableColumn<>(getResourceString("name", "Name"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Route route = event.getRowValue();
            String oldName = route.getName();
            route.setName(event.getNewValue());
            if (!updateRouteOnServer(route)) {
                route.setName(oldName);
                collectionTable.refresh();
            }
        });

        // X coordinate column
        TableColumn<Route, Double> xColumn = new TableColumn<>(getResourceString("x_coordinate", "X"));
        xColumn.setCellValueFactory(cellData -> {
            Route route = cellData.getValue();
            return new SimpleDoubleProperty(route.getCoordinates().getX()).asObject();
        });
        xColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        xColumn.setOnEditCommit(event -> {
            Route route = event.getRowValue();
            Coordinates oldCoords = route.getCoordinates();
            Coordinates newCoords = new Coordinates(event.getNewValue(), oldCoords.getY());
            route.setCoordinates(newCoords);
            if (!updateRouteOnServer(route)) {
                route.setCoordinates(oldCoords);
                collectionTable.refresh();
            }
        });

        // Y coordinate column
        TableColumn<Route, Float> yColumn = new TableColumn<>(getResourceString("y_coordinate", "Y"));
        yColumn.setCellValueFactory(cellData -> {
            Route route = cellData.getValue();
            return new SimpleFloatProperty(route.getCoordinates().getY()).asObject();
        });
        yColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        yColumn.setOnEditCommit(event -> {
            Route route = event.getRowValue();
            Coordinates oldCoords = route.getCoordinates();
            Coordinates newCoords = new Coordinates(oldCoords.getX(), event.getNewValue());
            route.setCoordinates(newCoords);
            if (!updateRouteOnServer(route)) {
                route.setCoordinates(oldCoords);
                collectionTable.refresh();
            }
        });

        TableColumn<Route, String> fromColumn = new TableColumn<>(getResourceString("from", "From"));
        fromColumn.setCellValueFactory(cellData -> {
            Route route = cellData.getValue();
            return new SimpleStringProperty(route.getFrom().getName());
        });
        fromColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: blue; -fx-underline: true;");
                    setOnMouseClicked(event -> {
                        Route route = getTableView().getItems().get(getIndex());
                        showLocationEditor(route.getFrom(), "From Location", route);
                    });
                }
            }
        });

        TableColumn<Route, String> toColumn = new TableColumn<>(getResourceString("to", "To"));
        toColumn.setCellValueFactory(cellData -> {
            Route route = cellData.getValue();
            return new SimpleStringProperty(route.getTo().getName());
        });
        toColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: blue; -fx-underline: true;");
                    setOnMouseClicked(event -> {
                        Route route = getTableView().getItems().get(getIndex());
                        showLocationEditor(route.getTo(), "To Location", route);
                    });
                }
            }
        });

        TableColumn<Route, Integer> distanceColumn = new TableColumn<>(getResourceString("distance", "Distance"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        distanceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        distanceColumn.setOnEditCommit(event -> {
            Route route = event.getRowValue();
            int oldDistance = route.getDistance();
            route.setDistance(event.getNewValue());
            if (!updateRouteOnServer(route)) {
                route.setDistance(oldDistance);
                collectionTable.refresh();
            }
        });

        collectionTable.setEditable(true);
        collectionTable.getColumns().addAll(idColumn, nameColumn, xColumn, yColumn, fromColumn, toColumn, distanceColumn);
    }

    private void showCoordinatesEditor(Route route) {
        Dialog<Coordinates> dialog = new Dialog<>();
        dialog.setTitle(getResourceString("edit_coordinates", "Edit Coordinates"));
        dialog.setHeaderText(null);

        TextField xField = new TextField(String.valueOf(route.getCoordinates().getX()));
        TextField yField = new TextField(String.valueOf(route.getCoordinates().getY()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("X:"), 0, 0);
        grid.add(xField, 1, 0);
        grid.add(new Label("Y:"), 0, 1);
        grid.add(yField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType(getResourceString("save", "Save"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    double x = Double.parseDouble(xField.getText());
                    float y = Float.parseFloat(yField.getText());
                    return new Coordinates(x, y);
                } catch (NumberFormatException e) {
                    showError(getResourceString("invalid_coordinates", "Invalid coordinates format"));
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newCoordinates -> {
            Coordinates oldCoordinates = route.getCoordinates();
            route.setCoordinates(newCoordinates);
            if (!updateRouteOnServer(route)) {
                route.setCoordinates(oldCoordinates);
                collectionTable.refresh();
            }
        });
    }

    private void showLocationEditor(Location location, String title, Route route) {
        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        TextField nameField = new TextField(location.getName());
        TextField xField = new TextField(String.valueOf(location.getX()));
        TextField yField = new TextField(String.valueOf(location.getY()));
        TextField zField = new TextField(String.valueOf(location.getZ()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("X:"), 0, 1);
        grid.add(xField, 1, 1);
        grid.add(new Label("Y:"), 0, 2);
        grid.add(yField, 1, 2);
        grid.add(new Label("Z:"), 0, 3);
        grid.add(zField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType(getResourceString("save", "Save"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    Long x = Long.parseLong(xField.getText());
                    long y = Long.parseLong(yField.getText());
                    Double z = Double.parseDouble(zField.getText());
                    return new Location(x, y, z, name);
                } catch (NumberFormatException e) {
                    showError(getResourceString("invalid_location", "Invalid location format"));
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newLocation -> {
            Location oldLocation = location;
            if (location == route.getFrom()) {
                route.setFrom(newLocation);
            } else {
                route.setTo(newLocation);
            }
            if (!updateRouteOnServer(route)) {
                if (location == route.getFrom()) {
                    route.setFrom(oldLocation);
                } else {
                    route.setTo(oldLocation);
                }
                collectionTable.refresh();
            }
        });
    }

    private boolean updateRouteOnServer(Route route) {
        if (networkManager.updateRoute(route)) {
            ExecutionResponse response = networkManager.receiveResponse();
            if (response.isSuccess()) {
                return true;
            } else {
                showError(response.getResponse());
            }
        }
        return false;
    }

    private void showCoordinatesDetails(Coordinates coordinates) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(getResourceString("coordinates_details", "Coordinates Details"));
        alert.setHeaderText(null);
        alert.setContentText(String.format(
            "X: %.2f\nY: %.2f",
            coordinates.getX(),
            coordinates.getY()
        ));
        alert.showAndWait();
    }

    private void showLocationDetails(Location location, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(String.format(
            "Name: %s\nX: %d\nY: %d\nZ: %.2f",
            location.getName(),
            location.getX(),
            location.getY(),
            location.getZ()
        ));
        alert.showAndWait();
    }

    private VBox createVisualizationContainer() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        visualizationTitle = new Label(getResourceString("visualization", "Visualization"));
        visualizationTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        visualizationCanvas = new Canvas(600, 700);
        visualizationCanvas.setStyle("-fx-background-color: #f8f8f8; -fx-background-radius: 5;");
        HBox.setHgrow(visualizationCanvas, Priority.ALWAYS);
        VBox.setVgrow(visualizationCanvas, Priority.ALWAYS);

        // Mouse events for pan
        visualizationCanvas.setOnMousePressed(e -> {
            dragging = true;
            dragStartX = e.getX();
            dragStartY = e.getY();
        });
        visualizationCanvas.setOnMouseReleased(e -> dragging = false);
        visualizationCanvas.setOnMouseDragged(e -> {
            if (dragging) {
                offsetXProperty.set(offsetXProperty.get() + (e.getX() - dragStartX));
                offsetYProperty.set(offsetYProperty.get() + (e.getY() - dragStartY));
                dragStartX = e.getX();
                dragStartY = e.getY();
                drawRoutes();
            }
        });
        // Mouse wheel for zoom
        visualizationCanvas.addEventHandler(ScrollEvent.SCROLL, e -> {
            double oldScale = scaleProperty.get();
            if (e.getDeltaY() > 0) scaleProperty.set(oldScale * 1.1);
            else scaleProperty.set(oldScale / 1.1);
            // Zoom to mouse position
            double mx = e.getX();
            double my = e.getY();
            offsetXProperty.set(mx - (mx - offsetXProperty.get()) * (scaleProperty.get() / oldScale));
            offsetYProperty.set(my - (my - offsetYProperty.get()) * (scaleProperty.get() / oldScale));
            drawRoutes();
        });

        container.getChildren().addAll(visualizationTitle, visualizationCanvas);
        return container;
    }

    private Button createCommandButton(String command, String text) {
        Button button = new Button(getResourceString(command, text));
        button.setStyle("-fx-background-color: #007aff; -fx-text-fill: white; -fx-background-radius: 10;");
        button.setOnAction(e -> handleCommand(command));
        return button;
    }

    private void handleCommand(String command) {
        switch (command) {
            case "add":
                handleAdd();
                break;
            case "remove":
                handleRemove();
                break;
            case "clear":
                handleClear();
                break;
            case "info":
                handleInfo();
                break;
        }
    }

    private void loadRoutes() {
        List<Route> serverRoutes = networkManager.getCollection();
        routes.clear();
        routes.addAll(serverRoutes);
        refreshVisualization();
    }

    private void handleAdd() {
        RouteDialog dialog = new RouteDialog(bundle, null);
        dialog.show();
        Route newRoute = dialog.getRoute();
        if (newRoute != null) {
            networkManager.addRoute(newRoute);
            ExecutionResponse response = networkManager.receiveResponse();
                if (response.isSuccess()) {
                    loadRoutes(); // Reload all routes after adding
                } else {
                    showError(response.getResponse());
                }
            }
        }


    private void handleRemove() {
        Route selectedRoute = collectionTable.getSelectionModel().getSelectedItem();
        if (selectedRoute != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getResourceString("confirm_removal", "Confirm Removal"));
            alert.setHeaderText(null);
            alert.setContentText(getResourceString("confirm_removal_text", "Are you sure you want to remove this route?"));
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (networkManager.removeRoute(selectedRoute.getId())) {
                    ExecutionResponse response = networkManager.receiveResponse();
                    if (response.isSuccess()) {
                        loadRoutes(); // Reload all routes after removing
                    } else {
                        showError(response.getResponse());
                    }
                }
            }
        } else {
            showError(getResourceString("no_selection", "Please select a route to remove"));
        }
    }

    private void handleClear() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(getResourceString("confirm_clear", "Confirm Clear"));
        alert.setHeaderText(null);
        alert.setContentText(getResourceString("confirm_clear_text", "Are you sure you want to clear the collection?"));
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (networkManager.clearCollection()) {
                ExecutionResponse response = networkManager.receiveResponse();
                if (response.isSuccess()) {
                    loadRoutes(); // Reload all routes after clearing
                } else {
                    showError(response.getResponse());
                }
            }
        }
    }

    private void handleInfo() {
        if (networkManager.getCollectionInfo()) {
            ExecutionResponse response = networkManager.receiveResponse();
            if (response.isSuccess()) {
                String info = response.getResponse();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(getResourceString("collection_info", "Collection Information"));
                alert.setHeaderText(null);
                alert.setContentText(info);
                alert.showAndWait();
            } else {
                showError(response.getResponse());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(getResourceString("error", "Error"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateUserLabel() {
        // This method is no longer needed as we removed the label
    }

    private void updateLanguage(String language) {
        Locale newLocale;
        switch (language) {
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
        updateUIText();
    }

    private void updateUIText() {
        // Update window title
        stage.setTitle(getResourceString("app_title", "Collection Client"));
        
        // Update user label
        updateUserLabel();
        
        // Update table and visualization titles
        tableTitle.setText(getResourceString("collection_table", "Collection"));
        visualizationTitle.setText(getResourceString("visualization", "Visualization"));
        
        // Update command buttons
        for (Map.Entry<String, Button> entry : commandButtons.entrySet()) {
            entry.getValue().setText(getResourceString(entry.getKey(), entry.getKey()));
        }
        
        // Update search and clear button text
        if (searchButton != null) searchButton.setText(getResourceString("search_button", "Search"));
        if (clearButton != null) clearButton.setText(getResourceString("clear_button", "Clear"));
        
        // Update table columns
        updateTableColumns();
    }

    private void updateTableColumns() {
        collectionTable.getColumns().clear();
        addTableColumns();
    }

    private String getResourceString(String key, String fallback) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return fallback;
        }
    }

    private void filterRoutesByName(String name) {
        if (networkManager.filterRoutesByName(name)) {
            ExecutionResponse response = networkManager.receiveResponse();
            if (response.isSuccess()) {
                List<Route> filteredRoutes = response.getRoutes();
                routes.clear();
                routes.addAll(filteredRoutes);
                refreshVisualization();
            } else {
                showError(response.getResponse());
            }
        }
    }

    private void drawRoutes() {
        var gc = visualizationCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, visualizationCanvas.getWidth(), visualizationCanvas.getHeight());
        
        // Find bounds for auto-scaling based on locations
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Route r : routes) {
            minX = Math.min(minX, Math.min(r.getFrom().getX(), r.getTo().getX()));
            minY = Math.min(minY, Math.min(r.getFrom().getY(), r.getTo().getY()));
            maxX = Math.max(maxX, Math.max(r.getFrom().getX(), r.getTo().getX()));
            maxY = Math.max(maxY, Math.max(r.getFrom().getY(), r.getTo().getY()));
        }
        double margin = 50;
        double w = visualizationCanvas.getWidth() - 2 * margin;
        double h = visualizationCanvas.getHeight() - 2 * margin;
        double dx = maxX - minX == 0 ? 1 : maxX - minX;
        double dy = maxY - minY == 0 ? 1 : maxY - minY;

        // Draw coordinate system
        drawCoordinateSystem(gc, minX, maxX, minY, maxY, margin, w, h);

        // Draw all routes
        for (Route r : routes) {
            boolean isSelected = r == selectedRoute;
            double alpha = isSelected ? 1.0 : 0.3;
            gc.setGlobalAlpha(alpha);

            // Calculate positions for from and to locations
            double fx = margin + ((r.getFrom().getX() - minX) / dx) * w;
            double fy = h - margin - ((r.getFrom().getY() - minY) / dy) * h; // Invert Y coordinate
            double tx = margin + ((r.getTo().getX() - minX) / dx) * w;
            double ty = h - margin - ((r.getTo().getY() - minY) / dy) * h; // Invert Y coordinate

            // Apply pan and zoom
            fx = fx * scaleProperty.get() + offsetXProperty.get();
            fy = fy * scaleProperty.get() + offsetYProperty.get();
            tx = tx * scaleProperty.get() + offsetXProperty.get();
            ty = ty * scaleProperty.get() + offsetYProperty.get();

            // Draw buildings at from and to locations
            drawSkyscraper(gc, fx, fy, r.getFrom(), isSelected);
            drawSkyscraper(gc, tx, ty, r.getTo(), isSelected);

            // Draw route line
            gc.setStroke(isSelected ? Color.web("#007aff") : Color.web("#888"));
            gc.setLineWidth(isSelected ? 4 : 2);
            gc.strokeLine(fx, fy, tx, ty);

            // Draw arrowhead
            double angle = Math.atan2(ty - fy, tx - fx);
            double arrowLen = 15 * scaleProperty.get();
            double ax1 = tx - arrowLen * Math.cos(angle - Math.PI / 6);
            double ay1 = ty - arrowLen * Math.sin(angle - Math.PI / 6);
            double ax2 = tx - arrowLen * Math.cos(angle + Math.PI / 6);
            double ay2 = ty - arrowLen * Math.sin(angle + Math.PI / 6);
            gc.strokeLine(tx, ty, ax1, ay1);
            gc.strokeLine(tx, ty, ax2, ay2);

            // Draw route label with name and distance
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("System", FontWeight.BOLD, 12 * scaleProperty.get()));
            String label = String.format("%s (%d)", r.getName(), r.getDistance());
            gc.fillText(label, (fx + tx) / 2, (fy + ty) / 2 - 10 * scaleProperty.get());
        }

        // Draw person icon at coordinates
        for (Route r : routes) {
            if (r == selectedRoute) {  // Only draw person for selected route
                double px = margin + ((r.getCoordinates().getX() - minX) / dx) * w;
                double py = h - margin - ((r.getCoordinates().getY() - minY) / dy) * h; // Invert Y coordinate
                px = px * scaleProperty.get() + offsetXProperty.get();
                py = py * scaleProperty.get() + offsetYProperty.get();
                drawPerson(gc, px, py, r.getId(), true);
            }
        }

        gc.setGlobalAlpha(1.0);
    }

    private void drawCoordinateSystem(GraphicsContext gc, double minX, double maxX, double minY, double maxY, 
                                    double margin, double w, double h) {
        // Empty method - grid drawing removed
    }

    private double calculateStepSize(double range) {
        double rawStep = range / 8; // Aim for about 8 grid lines
        double magnitude = Math.pow(10, Math.floor(Math.log10(rawStep)));
        double normalizedStep = rawStep / magnitude;
        
        if (normalizedStep < 1.5) return magnitude;
        if (normalizedStep < 3) return 2 * magnitude;
        if (normalizedStep < 7.5) return 5 * magnitude;
        return 10 * magnitude;
    }

    private void drawSkyscraper(GraphicsContext gc, double x, double y, Location location, boolean isSelected) {
        double baseSize = 25 * scaleProperty.get();
        int floors = (int)(Math.abs(location.getZ()) * 2) + 1;
        double floorHeight = 15 * scaleProperty.get();
        double totalHeight = floors * floorHeight;
        
        // Skyscraper color based on selection
        Color buildingColor = isSelected ? Color.web("#007aff") : Color.web("#2c3e50");
        Color windowColor = isSelected ? Color.web("#87ceeb") : Color.web("#34495e");
        
        // Draw main building structure
        gc.setFill(buildingColor);
        gc.fillRect(x - baseSize/2, y - totalHeight, baseSize, totalHeight);
        
        // Draw windows for each floor
        gc.setFill(windowColor);
        double windowSize = baseSize/4;
        double windowSpacing = baseSize/3;
        
        for (int floor = 0; floor < floors; floor++) {
            double floorY = y - totalHeight + (floor * floorHeight) + floorHeight/3;
            
            // Draw windows in a grid pattern
            for (int col = 0; col < 2; col++) {
                double windowX = x - baseSize/3 + (col * windowSpacing);
                gc.fillRect(windowX, floorY, windowSize, windowSize);
            }
        }
        
        // Draw antenna/spire on top
        gc.setStroke(buildingColor);
        gc.setLineWidth(2 * scaleProperty.get());
        double antennaHeight = baseSize * 0.8;
        gc.strokeLine(x, y - totalHeight, x, y - totalHeight - antennaHeight);
        
        // Draw blinking light on antenna
        if (isSelected) {
            gc.setFill(Color.web("#ff3b30"));
            gc.fillOval(x - 3, y - totalHeight - antennaHeight - 3, 6, 6);
        }
    }

    private void drawPerson(GraphicsContext gc, double x, double y, long id, boolean isSelected) {
        double size = 20 * scaleProperty.get();
        
        // Draw person body - use blue color for selected route, dark gray for others
        gc.setFill(isSelected ? Color.web("#007aff") : Color.web("#2c3e50"));
        
        // Head
        gc.fillOval(x - size/4, y - size, size/2, size/2);
        
        // Body
        gc.fillRect(x - size/4, y - size/2, size/2, size);
        
        // Arms
        gc.setLineWidth(3 * scaleProperty.get());
        gc.strokeLine(x - size/4, y - size/3, x - size/2, y);
        gc.strokeLine(x + size/4, y - size/3, x + size/2, y);
        
        // Legs
        gc.strokeLine(x - size/4, y + size/2, x - size/4, y + size);
        gc.strokeLine(x + size/4, y + size/2, x + size/4, y + size);

        // Draw ID label
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("System", FontWeight.BOLD, 10 * scaleProperty.get()));
        gc.fillText(String.valueOf(id), x, y - size - 5 * scaleProperty.get());
    }

    private void refreshVisualization() {
        drawRoutes();
    }

    public void show() {
        stage.show();
    }

    private void animateFocusOnRoute(Route route) {
        selectedRoute = route;
        // Найти координаты выбранного маршрута
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Route r : routes) {
            minX = Math.min(minX, Math.min(r.getFrom().getX(), r.getTo().getX()));
            minY = Math.min(minY, Math.min(r.getFrom().getY(), r.getTo().getY()));
            maxX = Math.max(maxX, Math.max(r.getFrom().getX(), r.getTo().getX()));
            maxY = Math.max(maxY, Math.max(r.getFrom().getY(), r.getTo().getY()));
        }
        double margin = 50;
        double w = visualizationCanvas.getWidth() - 2 * margin;
        double h = visualizationCanvas.getHeight() - 2 * margin;
        double dx = maxX - minX == 0 ? 1 : maxX - minX;
        double dy = maxY - minY == 0 ? 1 : maxY - minY;
        double fx = margin + ((route.getFrom().getX() - minX) / dx) * w;
        double fy = h - margin - ((route.getFrom().getY() - minY) / dy) * h;
        double tx = margin + ((route.getTo().getX() - minX) / dx) * w;
        double ty = h - margin - ((route.getTo().getY() - minY) / dy) * h;
        
        // Центр маршрута
        double centerX = (fx + tx) / 2;
        double centerY = (fy + ty) / 2;
        
        // Желаемый масштаб и смещение
        double targetScale = 2.0;
        double canvasCenterX = visualizationCanvas.getWidth() / 2;
        double canvasCenterY = visualizationCanvas.getHeight() / 2;
        double targetOffsetX = canvasCenterX - centerX * targetScale;
        double targetOffsetY = canvasCenterY - centerY * targetScale;
        
        // Устанавливаем значения напрямую без анимации
        scaleProperty.set(targetScale);
        offsetXProperty.set(targetOffsetX);
        offsetYProperty.set(targetOffsetY);
        
        drawRoutes();
    }
}