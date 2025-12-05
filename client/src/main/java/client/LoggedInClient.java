package client;

import chess.ChessGame;
import model.GameData;
import requestresult.*;
import serverfacade.ServerFacade;
import ui.DrawBoard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class LoggedInClient implements Client{
    private final ServerFacade server;
    private final String authToken;
    private final Map<Integer, GameData> gameList;
    private ChessGame.TeamColor color;
    private ChessGame game;
    private int id;

    public LoggedInClient(String serverUrl, String authToken) {
        server = new ServerFacade(serverUrl);
        this.authToken = authToken;
        gameList = new HashMap<>();
        listGames();
        ChessGame.TeamColor color = null;
        ChessGame game = null;
        id = 0;
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
            if (ex.getMessage().equals("HTTP 403: {\"message\":\"Error: White Already Taken\"}")) {
                return "Player position already taken";
            }
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
            try {
                var result = server.listGames(authToken);
                gameList.put(gameList.size() + 1, result.games().getLast());
            } catch (Exception ex) {
                return ex.getMessage();
            }
            return "New Game Created!";
        }
        throw new Exception("Expected: <NAME>");
    }

    public String listGames() {
        if (!gameList.isEmpty()) {
            gameList.clear();
        }
        GetGameListResult result = null;
        try {
            result = server.listGames(authToken);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        var list = result.games();
        if (list.isEmpty()) {
            return "No games";
        }
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (var game : list) {
            gameList.put(i, game);
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
            sb.append("Game ID: ").append(i).append("  Game Name: ").append(game.gameName())
                    .append("  White Player: ").append(wU).append("  Black Player: ").append(bU).append("\n");
            i++;
        }
        return sb.toString();
    }

    public String playGame(String[] params) throws Exception {
        if (params.length == 2) {
            try {
                Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new Exception("Please enter an id number");
            }
            id = Integer.parseInt(params[0]);
            if (gameList.get(id).game().isGameFinished()) {
                throw new Exception("Game is finished");
            }
            if (gameList.get(id) == null) {
                throw new Exception("No game with given id");
            }
            if (Objects.equals(params[1], "white")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(params[1], "black")) {
                color = ChessGame.TeamColor.BLACK;
            }
            try {
                server.playGame(new JoinGameRequest(authToken, color, gameList.get(id).gameID()));
            } catch (Exception ex) {
                if (ex.getMessage().equals("HTTP 403: {\"message\":\"Error: White Already Taken\"}")) {
                    return "Player position already taken";
                } else if (ex.getMessage().equals("HTTP 400: {\"message\":\"Error: Bad Request\"}")) {
                    return "Choose White or Black";
                } else if (ex.getMessage().equals("HTTP 400: {\"message\":\"Error: No Game with given ID\"}")) {
                    return "No game with given id";
                }
                return ex.getMessage();
            }
        } else {
            throw new Exception("Expected: <ID> [WHITE|BLACK]");
        }
        this.game = server.listGames(authToken).games().get(gameList.get(id).gameID() - 1).game();
        return DrawBoard.render(game.getBoard(), color, null);
    }

    public String observeGame(String[] params) throws Exception {
        if (params.length == 1) {
            try {
                Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new Exception("Please enter an id number");
            }
            id = Integer.parseInt(params[0]);
            if (gameList.get(id) == null) {
                throw new Exception("No game with given id");
            }
            try {
                server.observeGame(new JoinGameRequest(authToken, null, gameList.get(id).gameID()));
            } catch (Exception ex) {
                if (ex.getMessage().equals("HTTP 400: {\"message\":\"Error: No Game with given ID\"}")) {
                    return "No game with given id";
                }
            }
        } else {
            throw new Exception("Expected: <ID>");
        }
        color = ChessGame.TeamColor.WHITE;
        this.game = server.listGames(authToken).games().get(gameList.get(id).gameID() - 1).game();
        return DrawBoard.render(game.getBoard(), color, null);
    }

    public String logout() {
        try {
            server.logout(new LogoutRequest(authToken));
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return "You have Successfully been Logged Out!";
    }

    @Override
    public ChessGame.TeamColor getColor() {
        return color;
    }

    @Override
    public ChessGame getGame() {
        return game;
    }

    @Override
    public int getId() {
        return id;
    }
}
