package server;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, SessionHolder> connections = new ConcurrentHashMap<>();

    public void add(int id, Session session, ChessGame.TeamColor color) {
        var holder = connections.get(id);
        if (holder == null) {
            System.out.println("Creating new holder");
            holder = new SessionHolder();
        }
        System.out.println("adding session for  " + id + " " + color);
        holder.setSession(session, color);
        connections.put(id, holder);
    }

    public void add(int id, Session session, String name) {
        var holder = connections.get(id);
        if (holder == null) {
            System.out.println("Creating new holder");
            holder = new SessionHolder();
        }
        System.out.println("adding session for  " + id);
        holder.setSession(session, name);
        connections.put(id, holder);
    }

    public void remove(int id) {
        connections.remove(id);
    }

    public void broadcast(int gameId, ServerMessage message) throws IOException {
        var serializer = new Gson();
        var serializedMessage = serializer.toJson(message);
        var holder = connections.get(gameId);
        var bSession = holder.getBSession();
        var wSession = holder.getWSession();
        if(bSession != null){
            bSession.getRemote().sendString(serializedMessage);
        }
        if(wSession != null){
            wSession.getRemote().sendString(serializedMessage);
        }
    }

    public void broadcast(int gameId, ServerMessage message, ChessGame.TeamColor color) throws IOException {
        var serializer = new Gson();
        var serializedMessage = serializer.toJson(message);
        var holder = connections.get(gameId);
        var bSession = holder.getBSession();
        var wSession = holder.getWSession();
        if (bSession != null && color.equals(ChessGame.TeamColor.BLACK)) {
            bSession.getRemote().sendString(serializedMessage);
        }
        if (wSession != null && color.equals(ChessGame.TeamColor.WHITE)) {
            wSession.getRemote().sendString(serializedMessage);
        }
    }

    public void broadcast(int gameId, String name, ServerMessage message, ChessGame.TeamColor color) throws IOException {
        var serializer = new Gson();
        var serializedMessage = serializer.toJson(message);
        var holder = connections.get(gameId);
        var bSession = holder.getBSession();
        var wSession = holder.getWSession();
        var oSessions = holder.getOSessions(name);
        if (bSession != null && color.equals(ChessGame.TeamColor.BLACK)) {
            bSession.getRemote().sendString(serializedMessage);
        }
        if (wSession != null && color.equals(ChessGame.TeamColor.WHITE)) {
            wSession.getRemote().sendString(serializedMessage);
        }
        if (oSessions != null) {
            for (var sessions : oSessions.values()) {
                sessions.getRemote().sendString(serializedMessage);
            }
        }
    }

    public void broadcast(int gameId, ServerMessage message, String name) throws IOException {
        var serializer = new Gson();
        var serializedMessage = serializer.toJson(message);
        var holder = connections.get(gameId);
        var oSession = holder.getOSession(name);
        if (oSession != null) {
            oSession.getRemote().sendString(serializedMessage);
        }
    }
}