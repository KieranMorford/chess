package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;
import websocket.commands.UserGameCommand;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String message;
    UserGameCommand.CommandType commandType;
    ChessGame game;
    String errorMessage;
    ChessGame.TeamColor color;
    ChessMove move;
    String username;
    String losername;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        this.message = message;
    }
    public ServerMessage(ServerMessageType type, String message,  UserGameCommand.CommandType commandType) {
        this.serverMessageType = type;
        this.message = message;
        this.commandType = commandType;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public void setColor(ChessGame.TeamColor color) {
        this.color = color;
    }

    public ChessGame.TeamColor getColor() {
        return this.color;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    public ChessMove getMove() {
        return this.move;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setLosername(String losername) {
        this.losername = losername;
    }

    public String getLosername() {
        return this.losername;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public UserGameCommand.CommandType getCommandType() {
        return this.commandType;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
