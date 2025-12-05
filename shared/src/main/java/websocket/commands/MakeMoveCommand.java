package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class MakeMoveCommand extends UserGameCommand {

    private ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.CONNECT, authToken, gameID);
        this.move = move;
    }
}
