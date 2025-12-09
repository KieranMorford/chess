package server;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;

public class SessionHolder {
    private Session wSession;
    private Session bSession;
    private Map<String, Session> oSessions;

    public SessionHolder() {}

    public void setSession(Session session, ChessGame.TeamColor color) {
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            System.out.println("Setting white session");
            wSession = session;
        } else if (color.equals(ChessGame.TeamColor.BLACK)) {
            System.out.println("Setting black session");
            bSession = session;
        }
    }

    public void setSession(Session session, String name) {
        System.out.println("Setting observer session");
        oSessions.put(name, session);
    }

    public void setWSession(Session wSession) {
        this.wSession = wSession;
    }

    public void setBSession(Session bSession) {
        this.bSession = bSession;
    }

    public Session getWSession() {
        return wSession;
    }

    public Session getBSession() {
        return bSession;
    }

    public Session getOSession(String name) {
        Session oSession = null;
        if (oSessions != null && oSessions.containsKey(name)) {
            oSession = oSessions.get(name);
        }
        return oSession;
    }

    public Map<String, Session> getOSessions(String name) {
        if (oSessions != null && oSessions.containsKey(name)) {
            oSessions.remove(name);
        }
        return oSessions;
    }
}
