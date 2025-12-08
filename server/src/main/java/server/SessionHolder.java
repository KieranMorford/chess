package server;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

public class SessionHolder {
    private Session wSession;
    private Session bSession;

    public SessionHolder() {}

    public void setSession(Session session, ChessGame.TeamColor color) {
        if(color.equals(ChessGame.TeamColor.WHITE)) {
            System.out.println("Setting white session∂");
            wSession = session;
        }else{
            System.out.println("Setting black session∂");
            bSession = session;
        }
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
}
