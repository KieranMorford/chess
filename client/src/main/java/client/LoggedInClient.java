package client;

import chess.ChessGame;
import requestresult.*;
import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class LoggedInClient implements Client{
    private final ServerFacade server;
    private final String authToken;

    public LoggedInClient(String serverUrl, String authToken) {
        server = new ServerFacade(serverUrl);
        this.authToken = authToken;
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
                case "join" -> playGame(params);
                case "observe" -> observeGame(params);
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

    @Override
    public String getAuthToken() {
        return authToken;
    }

    public String createGame(String[] params) throws Exception {
        if (params.length == 1) {
            String name = params[0];
            try {
                server.createGame(new NewGameRequest(authToken, name));
            } catch (Exception ex) {
                return ex.getMessage();
            }
            return "New Game Created!";
        }
        throw new Exception("Expected: <NAME>");
    }

    public String listGames() {
        GetGameListResult result = null;
        try {
            result = server.listGames(authToken);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        var list = result.games();
        StringBuilder sb = new StringBuilder();
        for (var game : list) {
            String wU;
            String bU;
            if (game.whiteUsername() == null) {
                wU = "No Player";
            } else {
                wU = game.whiteUsername();
            }
            if (game.blackUsername() == null) {
                bU = "No Player";
            } else {
                bU = game.blackUsername();
            }
            sb.append("Game ID: ").append(game.gameID()).append("  Game Name: ").append(game.gameName())
                    .append("  White Player: ").append(wU).append("  Black Player: ").append(bU).append("\n");
        }
        return sb.toString();
    }

    public String playGame(String[] params) throws Exception {
        if (params.length == 2) {
            int id = Integer.parseInt(params[0]);
            ChessGame.TeamColor color = null;
            if (Objects.equals(params[1], "white")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(params[1], "black")) {
                color = ChessGame.TeamColor.BLACK;
            }
            JoinGameResult result = null;
            try {
                result = server.playGame(new JoinGameRequest(authToken, color, id));
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        return "joined game";
    }

    public String observeGame(String[] params) throws Exception {
        if (params.length == 1) {
            int id = Integer.parseInt(params[0]);
            JoinGameResult result = null;
            try {
                result = server.observeGame(new JoinGameRequest(authToken, null, id));
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        return "observed";
    }

    public String logout() {
        try {
            server.logout(new LogoutRequest(authToken));
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return "You have Successfully been Logged Out!";
    }
}
