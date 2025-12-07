package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
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
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

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
    public void handleMessage(WsMessageContext ctx) {
        var serializer = new Gson();
        int gameId = -1;
        Session session = ctx.session;
        try {
            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = dataAccess.getAuth(command.getAuthToken()).username();
            connections.add(gameId, session);
            switch (command.getCommandType()) {
                case CONNECT -> connect(gameId, username, (UserGameCommand) command);
                case MAKE_MOVE -> {
                    MakeMoveCommand mCommand = serializer.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(gameId, username, (MakeMoveCommand) mCommand);
                }
                case LEAVE -> leaveGame(gameId, username, (UserGameCommand) command);
                case RESIGN -> resign(gameId, username, (UserGameCommand) command);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void connect(int gameId, String username, UserGameCommand command) throws IOException {
        connections.broadcast(gameId, username + " joined the game as " + command.get + ".");
    }

    private void makeMove(int gameId, String username, MakeMoveCommand mCommand) throws IOException {
        connections.broadcast(gameId, "GAME JOINED");
    }

    private void leaveGame(int gameId, String username, UserGameCommand command) throws IOException, BadRequestException, DataAccessException {
        var game = dataAccess.getGame(gameId);
        if (game.whiteUsername().equals(username)) {
            dataAccess.updateGame(new GameData(gameId, null, game.blackUsername(), game.gameName(), game.game()));
        } else if (game.blackUsername().equals(username)) {
            dataAccess.updateGame(new GameData(gameId, game.whiteUsername(), null, game.gameName(), game.game()));
        }
        connections.broadcast(gameId, username + " left the game.");
    }

    private void resign(int gameId, String username, UserGameCommand command) throws IOException {
        connections.broadcast(gameId, "RESIGN");
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
