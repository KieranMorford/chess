package server;

import org.eclipse.jetty.websocket.api.Session;

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

    public void broadcast(int gameId, String notification) throws IOException {
        connections.get(gameId).getRemote().sendString(notification);
    }
}