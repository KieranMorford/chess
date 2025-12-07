package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Session> connections = new ConcurrentHashMap<>();

    public void add(int id, Session session) {
        connections.put(id, session);
    }

    public void remove(int id) {
        connections.remove(id);
    }

    public void broadcast(int gameId, ServerMessage message) throws IOException {
        var serializer = new Gson();
        connections.get(gameId).getRemote().sendString(serializer.toJson(message));
    }
}