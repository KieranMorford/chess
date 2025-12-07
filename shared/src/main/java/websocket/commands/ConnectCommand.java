package websocket.commands;

import chess.ChessMove;

public class ConnectCommand extends UserGameCommand {

    private ChessMove move;

    public ConnectCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }
}
