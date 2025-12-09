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
        String username = null;
        try {
            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            gameId = command.getGameID();
            username = dataAccess.getAuth(command.getAuthToken()).username();
            connections.add(gameId, session, username);
            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand cCommand = serializer.fromJson(ctx.message(), ConnectCommand.class);
                    connect(gameId, username, (ConnectCommand) cCommand, session);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand mCommand = serializer.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(gameId, username, (MakeMoveCommand) mCommand, session);
                }
                case LEAVE -> leaveGame(gameId, username, (UserGameCommand) command);
                case RESIGN -> resign(gameId, username, (UserGameCommand) command);
            }
        } catch (InvalidMoveException ex) {
            var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Invalid Move!");
            connections.broadcastOne(gameId, message, username);
        } catch (BadRequestException ex) {
            var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Bad Request!");
            connections.broadcastOne(gameId, message, username);
        } catch (UnauthorizedException ex) {
            var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            message.setErrorMessage("Unauthorized!");
            username = "HORRIBLENOGOODVERYBAD";
            connections.add(gameId, session, username);
            connections.broadcastOne(gameId, message, username);
            connections.remove(gameId, session);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void connect(int gameId, String username, ConnectCommand command, Session session) throws IOException, BadRequestException, DataAccessException {
        ServerMessage message = null;
        if (command.getColor() == null) {
            message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " is observing the game.", command.getCommandType());
        } else {
            message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " joined the game as the " + command.getColor().toString() + " player." + "\n[GAME] >>> ", command.getCommandType());
        }
        var serializer = new Gson();
        var game = dataAccess.getGame(gameId);
        ServerMessage lGMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, command.getCommandType());
        lGMessage.setGame(game.game());
        connections.broadcastRest(gameId, message, username);
        connections.broadcastOne(gameId, lGMessage, username);
    }

    private void makeMove(int gameId, String username, MakeMoveCommand command, Session session) throws IOException, BadRequestException, DataAccessException, InvalidMoveException {
        boolean check = dataAccess.getGame(gameId).game().isGameFinished();
        if (!dataAccess.getGame(gameId).game().isGameFinished()) {
            var serializer = new Gson();
            var move = command.getMove();
            if (dataAccess.getGame(gameId).game().getBoard().getPiece(move.getStartPosition()) != null) {
                var game = dataAccess.getGame(gameId);
                var color = game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
                ServerMessage lGMessage = null;
                ServerMessage message = null;
                game.game().makeMove(move);
                var str = new StringBuilder();
                int s = move.getStartPosition().getColumn();
                int e = move.getEndPosition().getColumn();
                char[] alpha = "abcdefgh".toCharArray();
                var sCol = Character.toString(alpha[s]);
                var eCol = Character.toString(alpha[e]);
                str.append(username).append(" made a move from ").append(sCol).append(move.getStartPosition().getRow())
                        .append(" to ").append(eCol).append(move.getEndPosition().getRow());
                if (move.getPromotionPiece() == null) {
                    lGMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, command.getCommandType());
                    str.append(".");
                } else {
                    lGMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, command.getCommandType());
                    str.append(", and was promoted to ").append(move.getPromotionPiece().toString()).append(".");
                }
                lGMessage.setGame(game.game());
                dataAccess.updateGame(game);
                connections.broadcastAll(gameId, lGMessage);
                message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, str.toString(), command.getCommandType());
                connections.broadcastRest(gameId, message, username);
            } else {
                connections.broadcastOne(gameId, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move!"), username);
            }
        } else {
            connections.broadcastOne(gameId, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Game is already over!"), username);
        }
    }

    private void leaveGame(int gameId, String username, UserGameCommand command) throws IOException, BadRequestException, DataAccessException {
        var game = dataAccess.getGame(gameId);
        if (game.whiteUsername().equals(username)) {
            dataAccess.updateGame(new GameData(gameId, null, game.blackUsername(), game.gameName(), game.game()));
        } else if (game.blackUsername().equals(username)) {
            dataAccess.updateGame(new GameData(gameId, game.whiteUsername(), null, game.gameName(), game.game()));
        }
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " left the game.\n[GAME] >>> ", command.getCommandType());
        connections.broadcastRest(gameId, message, username);
    }

    private void resign(int gameId, String username, UserGameCommand command) throws IOException, BadRequestException, DataAccessException {
        if (dataAccess.getGame(gameId).game().isGameFinished()) {
            String winner = "Nobody";
            if (dataAccess.getGame(gameId).whiteUsername() != null && dataAccess.getGame(gameId).whiteUsername().equals(username)) {
                winner = dataAccess.getGame(gameId).blackUsername();
            } else if (dataAccess.getGame(gameId).blackUsername() != null && dataAccess.getGame(gameId).blackUsername().equals(username)) {
                winner = dataAccess.getGame(gameId).whiteUsername();
            }
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " forfeited the game." + winner + " wins!\n[GAME] >>> ", command.getCommandType());
            connections.broadcastRest(gameId, message, username);
        } else {
            connections.broadcastOne(gameId, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Game is already over!"), username);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}
