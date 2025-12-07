package client;

import chess.ChessGame;
import chess.ChessPosition;
import serverfacade.NotificationHandler;
import serverfacade.ServerFacade;
import serverfacade.WebSocketFacade;
import ui.DrawBoard;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class GameClient implements Client, NotificationHandler {

    private final ServerFacade server;
    private final WebSocketFacade  webSocketFacade;
    private final String authToken;
    private final ChessGame.TeamColor color;
    private final ChessGame game;
    private final int id;

    public GameClient(String serverUrl, String authToken, ChessGame game, int id, ChessGame.TeamColor color) {
        server = new ServerFacade(serverUrl);
        webSocketFacade = new WebSocketFacade(serverUrl, this);
        this.authToken = authToken;
        this.color = color;
        this.game = game;
        this.id = id;

        this.webSocketFacade.SendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, this.authToken, this.id));
    }

    @Override
    public void notify(ServerMessage notification) {
        if (UserGameCommand.CommandType.RESIGN.equals(notification.getMessage()) && !this.game.isGameFinished()) {
            System.out.println("YOU WIN!");
        }
        System.out.println("THIS IS A WEBSOCKET! YAY!");
        System.out.println(RESET_TEXT_COLOR + notification.getMessage());
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "board" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> forfeitGame();
                case "highlight" -> highlightMoves(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE + "- board" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - display the board" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- leave" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - leave the game" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- move <COLROW> <COLROW>" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - move a piece (ex: move b2 b4)" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- resign" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - forfeit the game" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- highlight <COLROW>" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - highlight the moves a piece can make (ex: highlight b2)" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- help" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - display possible commands" + RESET_TEXT_ITALIC;
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    public String redrawBoard() throws Exception {
        return DrawBoard.render(game.getBoard(), color, null);
    }

    public String leaveGame() {
        this.webSocketFacade.SendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, this.authToken, this.id));
        return "You left the game.";
    }

    public String forfeitGame() {
        this.webSocketFacade.SendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, this.authToken, this.id));
        game.endGame();

        return "You forfeited the game.";
    }

    public String makeMove(String[] params) {
        if (game.isGameFinished()) {
            return "The game has already ended.";
        }
        this.webSocketFacade.SendCommand(new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, this.authToken, this.id));
        return "You made your move.";
    }

    public String highlightMoves(String[] params) throws Exception {
        String col = null;
        int row = 0;
        ChessPosition pos = null;
        if (params.length == 1) {
            try {
                params[0].substring(0,1).matches("\\p{Alpha}+");
                Integer.parseInt(params[0].substring(1));
            } catch (NumberFormatException e) {
                throw new Exception("Please enter a position in the format: a1");
            }
            col = params[0].substring(0,1);
            row = Integer.parseInt(params[0].substring(1));
            char cCol = col.charAt(0);
            int nCol = cCol - 'a' + 1;
            if (nCol < 1 || nCol > 8 || row < 1 || row > 8) {
                throw new Exception("Please enter a valid board position");
            }
            pos = new ChessPosition(row, nCol);
            if (game.getBoard().getPiece(pos) == null) {
                throw new Exception("No piece in selected spot");
            }
        } else {
            throw new Exception("Expected: <COLROW>");
        }
        return DrawBoard.render(game.getBoard(), color, pos);
    }

    @Override
    public ChessGame.TeamColor getColor() {
        return null;
    }

    @Override
    public ChessGame getGame() {
        return null;
    }

    @Override
    public int getId() {
        return id;
    }
}
