package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import envelopes.MakeMoveData;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.ConnectCommand;
import websocket.commands.GameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException {
        var serializer = new Gson();
        int gameId = -1;
        Session session = ctx.session;
        try {
            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = dataAccess.getAuth(command.getAuthToken()).username();
            System.out.println("Saving to connections, Game ID: " + gameId);
            ChessGame.TeamColor color = null;
            var blackUsername = dataAccess.getGame(gameId).blackUsername();
            if (blackUsername != null && blackUsername.equals(username)) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                color = ChessGame.TeamColor.WHITE;
            }
            System.out.println("Saving to connections, for color: " + color);
            connections.add(gameId, session, color);
            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand cCommand = serializer.fromJson(ctx.message(), ConnectCommand.class);
                    connect(gameId, username, (ConnectCommand) cCommand);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand mCommand = serializer.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(gameId, username, (MakeMoveCommand) mCommand);
                }
                case LEAVE -> leaveGame(gameId, username, (UserGameCommand) command);
                case RESIGN -> resign(gameId, username, (UserGameCommand) command);
            }
        } catch (InvalidMoveException ex) {
            connections.broadcast(gameId, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move!"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void connect(int gameId, String username, ConnectCommand command) throws IOException, BadRequestException, DataAccessException {
//        ServerMessage message = null;
//        if (command.getColor() != null) {
//            message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " joined the game as the " + command.getColor().toString() + " player.", command.getCommandType());
//        } else {
//            message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " is observing the game.", command.getCommandType());
//
//        }
//        connections.broadcast(gameId, message);
        var serializer = new Gson();
        var game = dataAccess.getGame(gameId);
        ChessGame.TeamColor color = null;
        ChessGame.TeamColor iColor = null;
        if (username.equals(game.whiteUsername())) {
            color = ChessGame.TeamColor.WHITE;
        } else if  (username.equals(game.blackUsername())) {
            color = ChessGame.TeamColor.BLACK;
        }
        ServerMessage lGMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, command.getCommandType());
        lGMessage.setGame(game.game());
        if (username.equals(game.whiteUsername())) {
            iColor = ChessGame.TeamColor.BLACK;
        } else if  (username.equals(game.blackUsername())) {
            iColor = ChessGame.TeamColor.WHITE;
        }
        connections.broadcast(gameId, lGMessage, color);
    }

    private void makeMove(int gameId, String username, MakeMoveCommand command) throws IOException, BadRequestException, DataAccessException, InvalidMoveException {
        var serializer = new Gson();
        var move = command.getMove();
        if (dataAccess.getGame(gameId).game().getBoard().getPiece(move.getStartPosition()) != null) {
            var game = dataAccess.getGame(gameId);
            var color = game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
            ServerMessage message = null;
            game.game().makeMove(move);
            if (move.getPromotionPiece() != null) {
                message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, serializer.toJson(new MakeMoveData(game, move, username, color)), command.getCommandType());
            } else {
                message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, serializer.toJson(new MakeMoveData(game, move, username, color)), command.getCommandType());
            }
            dataAccess.updateGame(game);
            connections.broadcast(gameId, message);
        } else {
            connections.broadcast(gameId, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move!"));
        }
    }

    private void leaveGame(int gameId, String username, UserGameCommand command) throws IOException, BadRequestException, DataAccessException {
        var game = dataAccess.getGame(gameId);
        if (game.whiteUsername().equals(username)) {
            dataAccess.updateGame(new GameData(gameId, null, game.blackUsername(), game.gameName(), game.game()));
        } else if (game.blackUsername().equals(username)) {
            dataAccess.updateGame(new GameData(gameId, game.whiteUsername(), null, game.gameName(), game.game()));
        }
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " left the game.", command.getCommandType());
        connections.broadcast(gameId, message);
    }

    private void resign(int gameId, String username, UserGameCommand command) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " forfeited the game. You win!", command.getCommandType());
        connections.broadcast(gameId, message);
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
