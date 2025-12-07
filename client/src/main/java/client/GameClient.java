package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import envelopes.MakeMoveData;
import serverfacade.NotificationHandler;
import serverfacade.ServerFacade;
import serverfacade.WebSocketFacade;
import ui.DrawBoard;
import ui.REPL;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class GameClient implements Client, NotificationHandler {

    private final REPL repl;
    private final ServerFacade server;
    private final WebSocketFacade  webSocketFacade;
    private final String authToken;
    private final ChessGame.TeamColor color;
    private ChessGame game;
    private final int id;

    public GameClient(REPL repl, String serverUrl, String authToken, ChessGame game, int id, ChessGame.TeamColor color) {
        this.repl = repl;
        server = new ServerFacade(serverUrl);
        webSocketFacade = new WebSocketFacade(serverUrl, this);
        this.authToken = authToken;
        this.color = color;
        this.game = game;
        this.id = id;

        this.webSocketFacade.SendCommand(new ConnectCommand(this.authToken, this.id, color));
    }

    @Override
    public void notify(ServerMessage notification) {
        var serializer = new Gson();
        if (notification.getCommandType().equals(UserGameCommand.CommandType.RESIGN) && this.game.isGameFinished()) {

        } else if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            var moveData = serializer.fromJson(notification.getMessage(), MakeMoveData.class);
            var str = new StringBuilder();
            if (notification.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
                str.append("\n").append(moveData.getUsername()).append(" made a move from ")
                        .append(moveData.getMove().getStartPosition().toString()).append(" to ")
                        .append(moveData.getMove().getEndPosition().toString());
                if (moveData.getMove().getPromotionPiece() != null) {
                    str.append(", and was promoted to ").append(moveData.getMove().getPromotionPiece().toString()).append(".");
                } else {
                    str.append(".");
                }
            }
            var board = DrawBoard.render(moveData.getGame().game().getBoard(), moveData.getColor(), null);
            repl.printToConsole(board);
            repl.printToConsole(str.toString());
        } else {
            repl.printToConsole(notification.getMessage());
        }
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
                + SET_TEXT_COLOR_BLUE + "- move <COLROW> <COLROW> (<PROMOTION> if valid)" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - move a piece (ex: move b7 b8 queen)" + RESET_TEXT_ITALIC + "\n"
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

    public String makeMove(String[] params) throws Exception {
        if (game.isGameFinished()) {
            return "The game has already ended.";
        }
        String col1 = null;
        int row1 = 0;
        String col2 = null;
        int row2 = 0;
        ChessPosition pos1 = null;
        ChessPosition pos2 = null;
        ChessPiece.PieceType promotion = null;
        ChessMove move = null;
        if (params.length == 2 || params.length == 3) {
            try {
                params[0].substring(0,1).matches("\\p{Alpha}+");
                Integer.parseInt(params[0].substring(1));
                params[1].substring(0,1).matches("\\p{Alpha}+");
                Integer.parseInt(params[1].substring(1));
            } catch (NumberFormatException e) {
                throw new Exception("Please enter a position in the format: a1");
            }
            col1 = params[0].substring(0,1);
            row1 = Integer.parseInt(params[0].substring(1,2));
            char cCol1 = col1.charAt(0);
            int nCol1 = cCol1 - 'a' + 1;
            col2 = params[1].substring(0,1);
            row2 = Integer.parseInt(params[1].substring(1,2));
            char cCol2 = col2.charAt(0);
            int nCol2 = cCol2 - 'a' + 1;
            if (nCol1 < 1 || nCol1 > 8 || row1 < 1 || row1 > 8 || nCol2 < 1 || nCol2 > 8 || row2 < 1 || row2 > 8) {
                throw new Exception("Please enter valid board positions");
            }
            if (params.length == 3) {
                promotion = ChessPiece.PieceType.valueOf(params[2]);
            }
            pos1 = new ChessPosition(row1, nCol1);
            pos2 = new ChessPosition(row2, nCol2);
            move = new ChessMove(pos1, pos2, promotion);
            if (!game.getBoard().getPiece(pos1).pieceMoves(game.getBoard(), pos1).contains(move)) {
                throw new Exception("Invalid move.");
            }
        } else {
            throw new Exception("Expected: <COLROW> <COLROW> (<PROMOTION> if valid)");
        }
        this.webSocketFacade.SendCommand(new MakeMoveCommand(this.authToken, this.id, move));

        return "";
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
