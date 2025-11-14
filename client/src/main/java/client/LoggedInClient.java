package client;

import requestresult.RegisterRequest;
import serverfacade.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class LoggedInClient implements Client{
    private final ServerFacade server;

    public LoggedInClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE + "- create <NAME>" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - create a game" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- list" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - list all games" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- join <ID> [WHITE|BLACK]" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - join a game" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- observe <ID>" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - observe a game" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- logout" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - logout of your chess account" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- quit" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - quit playing chess" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- help" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - display possible commands" + RESET_TEXT_ITALIC;
    }

    public String createGame(String[] params) throws Exception {
//        if (params.length == 3) {
//            String username = params[0];
//            String password = params[1];
//            String email = params[2];
//            server.register(new RegisterRequest(username, password, email));
//            return "Registered Successfully! You are now Logged in!";
//        }
//        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
        return "game made";
    }

    public String listGames() throws Exception {
        return "game list";
    }

    public String joinGame(String[] params) throws Exception {
        return "joined game";
    }

    public String observe(String[] params) throws Exception {
        return "observed";
    }

    public String logout() throws Exception {
        return "logged out";
    }
}
