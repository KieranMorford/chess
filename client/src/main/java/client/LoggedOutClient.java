package client;

import requestresult.RegisterRequest;
import serverfacade.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class LoggedOutClient implements Client {
    private final ServerFacade server;

    public LoggedOutClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE + "- register <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - create an account and login" + RESET_TEXT_ITALIC + "\n"
               + SET_TEXT_COLOR_BLUE + "- login <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - login with your chess account" + RESET_TEXT_ITALIC + "\n"
               + SET_TEXT_COLOR_BLUE + "- quit" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - quit playing chess" + RESET_TEXT_ITALIC + "\n"
               + SET_TEXT_COLOR_BLUE + "- help" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - display possible commands" + RESET_TEXT_ITALIC;
    }

    public String register(String[] params) throws Exception {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            server.register(new RegisterRequest(username, password, email));
            return "Registered Successfully! You are now Logged in!";
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String[] params) throws Exception {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
        }
        throw new Exception("Expected: <username><password>");
    }
}
