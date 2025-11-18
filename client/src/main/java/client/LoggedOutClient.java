package client;

import exceptions.RequestException;
import requestresult.*;
import serverfacade.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class LoggedOutClient implements Client {
    private final ServerFacade server;
    private String authToken;

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
            if (ex.getMessage().equals("HTTP 401: {\"message\":\"Error: Unauthorized\"}")) {
                return "Incorrect username or password";
            } else if (ex.getMessage().equals("HTTP 403: {\"message\":\"Error: Username Already Taken\"}")) {
                return "Username already taken";
            }
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
            try {
                var result = server.register(new RegisterRequest(username, password, email));
                this.authToken = result.authToken();
            } catch (RequestException ex) {
                return ex.getMessage();
            }
            return "Registered Successfully! You are now Logged in!";
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String[] params) throws Exception {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            try {
                var result = server.login(new LoginRequest(username, password));
                this.authToken = result.authToken();
            } catch (RequestException ex) {
                return ex.getMessage();
            }
            return "Logged In Successfully!";
        }
        throw new Exception("Expected: <username> password>");
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }
}
