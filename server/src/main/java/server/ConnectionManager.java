package server;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.UnauthorizedException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Map<String, Session>> connections = new ConcurrentHashMap<>();

    public void add(int id, Session session, String name) {
        var holder = connections.get(id);
        if (holder == null) {
            connections.put(id,  new ConcurrentHashMap<>());
            holder = connections.get(id);
        }
        System.out.println("adding session for  " + id);
        holder.put(name, session);
        connections.put(id, holder);
    }

    public void remove(int id, Session session) {
        var holder = connections.get(id);
        holder.remove(session);
    }

    public void remove(int id) {
        connections.remove(id);
    }

    public void broadcastAll(int gameId, ServerMessage message) throws IOException {
        var serializer = new Gson();
        var serializedMessage = serializer.toJson(message);
        var holder = connections.get(gameId);
        for (var session : holder.values()) {
            session.getRemote().sendString(serializedMessage);
        }
    }

    public void broadcastOne(int gameId, ServerMessage message, String name) throws IOException {
        var serializer = new Gson();
        var serializedMessage = serializer.toJson(message);
        var holder = connections.get(gameId);
        var session = holder.get(name);
        session.getRemote().sendString(serializedMessage);
    }

    public void broadcastRest(int gameId, ServerMessage message, String name) throws IOException {
        var serializer = new Gson();
        var serializedMessage = serializer.toJson(message);
        var holder = connections.get(gameId);
        var nSession = holder.get(name);
        for (var session : holder.values()) {
            if (!session.equals(nSession)) {
                session.getRemote().sendString(serializedMessage);
            }
        }
    }
}