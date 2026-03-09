package Gui;

import Command.CommandType;
import Command.CommandWithArgs;
import Authentication.User;
import Connection.ConnectionClient;
import Utility.ExecutionResponse;
import model.Route;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NetworkManager {
    private static final String HOST = "localhost";
    private static final int PORT = 1234;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RETRY_DELAY_MS = 5000;
    private ConnectionClient connectionClient;
    private User currentUser;
    private ResourceBundle bundle;

    public NetworkManager(ResourceBundle bundle) {
        this.bundle = bundle;
        this.connectionClient = new ConnectionClient(PORT, HOST);
    }

    public boolean connect() {
        return checkConnection();
    }

    private boolean checkConnection() {
        ExecutionResponse response;
        boolean connected = false;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            response = connectionClient.start();

            if (response.isSuccess()) {
                connected = true;
                break;
            }

            showError(String.format(getResourceString("connection_attempt", "Connection attempt %d/%d failed: %s"), 
                attempt, MAX_ATTEMPTS, response.getResponse()));

            if (attempt < MAX_ATTEMPTS) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        if (!connected) {
            showError(getResourceString("connection_failed", "Connection failed after " + MAX_ATTEMPTS + " attempts. Please try again later."));
            return false;
        }

        return true;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean sendCommand(CommandWithArgs command) {
        try {
            if (!checkConnection()) {
                return false;
            }
            byte[] serializedCommand = connectionClient.serializeObject(command);
            connectionClient.send(serializedCommand);
            return true;
        } catch (IOException e) {
            showError(getResourceString("connection_error", "Connection error: " + e.getMessage()));
            return false;
        }
    }

    public ExecutionResponse receiveResponse() {
        try {
            byte[] response = connectionClient.receive();
            if (response == null) {
                return new ExecutionResponse(getResourceString("connection_error", "No response from server"), false);
            }
            return connectionClient.deserializeObject(response);
        } catch (IOException | ClassNotFoundException e) {
            showError(getResourceString("connection_error", "Connection error: " + e.getMessage()));
            return new ExecutionResponse(getResourceString("connection_error", "Connection error: " + e.getMessage()), false);
        }
    }

    public List<Route> getCollection() {
        if (currentUser == null) {
            showError(getResourceString("user_not_set", "User not set"));
            return new ArrayList<>();
        }
        
        CommandWithArgs command = new CommandWithArgs(CommandType.SHOW, currentUser);
        if (sendCommand(command)) {
            ExecutionResponse response = receiveResponse();
            if (response.isSuccess()) {
                try {
                    List<Route> routes = response.getRoutes();
                    return routes;
                } catch (Exception e) {
                    showError(getResourceString("invalid_response", "Invalid response format from server: " + e.getMessage()));
                }
            } else {
                showError(response.getResponse());
            }
        }
        return new ArrayList<>();
    }


    public boolean addRoute(Route route) {
        if (currentUser == null) {
            showError(getResourceString("user_not_set", "User not set"));
            return false;
        }
        CommandWithArgs command = new CommandWithArgs(CommandType.ADD, route, currentUser);
        return sendCommand(command);
    }

    public boolean removeRoute(Long id) {
        if (currentUser == null) {
            showError(getResourceString("user_not_set", "User not set"));
            return false;
        }
        CommandWithArgs command = new CommandWithArgs(CommandType.REMOVE_BY_ID, id.toString(), currentUser);
        return sendCommand(command);
    }

    public boolean clearCollection() {
        if (currentUser == null) {
            showError(getResourceString("user_not_set", "User not set"));
            return false;
        }
        CommandWithArgs command = new CommandWithArgs(CommandType.CLEAR, currentUser);
        return sendCommand(command);
    }

    public boolean updateRoute(Route route) {
        if (currentUser == null) {
            showError(getResourceString("user_not_set", "User not set"));
            return false;
        }
        CommandWithArgs command = new CommandWithArgs(CommandType.UPDATE, route, currentUser);
        return sendCommand(command);
    }

    public boolean filterRoutesByName(String name) {
        if (currentUser == null) {
            showError(bundle.getString("user_not_set"));
            return false;
        }
        CommandWithArgs command = new CommandWithArgs(CommandType.FILTER_CONTAINS_NAME, name, currentUser);
        return sendCommand(command);
    }

    public boolean getCollectionInfo() {
        if (currentUser == null) {
            showError(bundle.getString("user_not_set"));
            return false;
        }
        CommandWithArgs command = new CommandWithArgs(CommandType.INFO, currentUser);
        return sendCommand(command);
    }

    private void showError(String message) {
        System.err.println("Error: " + message);
    }

    private String getResourceString(String key, String fallback) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return fallback;
        }
    }

    public void close() {
        try {
            if (connectionClient != null) {
                connectionClient.send(new byte[0]); // Send empty byte array to signal close
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 