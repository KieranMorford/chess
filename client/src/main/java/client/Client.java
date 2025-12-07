package client;

import chess.ChessGame;
import serverfacade.NotificationHandler;

public interface Client {
    public String eval(String input);
    public String help();
    public String getAuthToken();
    public ChessGame.TeamColor getColor();
    public ChessGame getGame();
    public int getId();
}
