package Command;

import Authentication.User;
import model.Route;

import java.io.Serializable;

public class CommandWithArgs implements Serializable {
    private  CommandType command;
    private String args;
    private Route route;
    private User user;

    public CommandWithArgs(CommandType command, String args) {
        this.command = command;
        this.args = args;
    }

    public CommandWithArgs(CommandType command) {
        this.command = command;
    }

    public CommandWithArgs(CommandType command, Route route) {
        this.command = command;
        this.route = route;
    }

    public CommandWithArgs(CommandType command, Route route, User user) {
        this.command = command;
        this.route = route;
        this.user = user;
    }

    public CommandWithArgs(CommandType command, String args, User user) {
        this.command = command;
        this.args = args;
        this.user = user;
    }

    public CommandWithArgs(CommandType command, User user) {
        this.command = command;
        this.user = user;
    }


    public CommandType getCommand() {
        return command;
    }
    public void setCommand(CommandType command) {
        this.command = command;
    }
    public String getArgs() {
        return args;
    }
    public void setArgs(String args) {
        this.args = args;
    }
    public Route getRoute() {
        return route;
    }
    public void setRoute(Route route) {
        this.route = route;
    }
    public User getUser() {
        return user;
    }
}
