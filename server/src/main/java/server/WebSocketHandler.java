package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exceptions.UnauthorizedException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
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
                case CONNECT -> connect(session, username, (UserGameCommand) command);
                case MAKE_MOVE -> {
                    MakeMoveCommand mCommand = serializer.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(session, username, (MakeMoveCommand) mCommand);
                }
                case LEAVE -> leaveGame(session, username, (UserGameCommand) command);
                case RESIGN -> resign(session, username, (UserGameCommand) command);
            }
        } catch (UnauthorizedException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect(Session session, String username, UserGameCommand command) {
//        connections.add(id, session);
    }

    private void makeMove(Session session, String username, MakeMoveCommand mCommand) {

    }

    private void leaveGame(Session session, String username, UserGameCommand command) {

    }

    private void resign(Session session, String username, UserGameCommand command) {

    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
