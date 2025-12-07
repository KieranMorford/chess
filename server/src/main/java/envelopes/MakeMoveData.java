package envelopes;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

public class MakeMoveData {
    private final ChessMove move;
    private final GameData game;
    private final String username;
    private final ChessGame.TeamColor color;

    public MakeMoveData(GameData game, ChessMove move, String username, ChessGame.TeamColor color) {
        this.game = game;
        this.move = move;
        this.username  = username;
        this.color = color;
    }

    public GameData getGame() {
        return game;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getUsername() {
        return username;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
