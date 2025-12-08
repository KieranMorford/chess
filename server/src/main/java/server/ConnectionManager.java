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
        System.out.println("adding seesion for  " + id + " " + color);
        holder.setSession(session, color);
        connections.put(id, holder);
    }

    public void remove(int id) {
        connections.remove(id);
    }

    public void broadcast(int gameId, ServerMessage message) throws IOException {
        var serializer = new Gson();
        var serializedMessage = serializer.toJson(message);
        var holder = connections.get(gameId);
        System.out.println("broadcasting " + serializedMessage + " to " + gameId);
        var bSession = holder.getBSession();
        var wSession = holder.getWSession();
        if(bSession != null){
            bSession.getRemote().sendString(serializedMessage);
        }
        if(wSession != null){
            wSession.getRemote().sendString(serializedMessage);
        }
    }
}