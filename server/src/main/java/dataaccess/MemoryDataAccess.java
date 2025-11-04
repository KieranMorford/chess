package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> userAuth = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        userAuth.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData user) {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        users.put(user.username(), new UserData(user.username(), hashedPassword, user.email()));
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData authData) {
        userAuth.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return userAuth.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        userAuth.remove(authToken);
    }

    @Override
    public List<GameData> listGames(String authToken) {
        if (userAuth.containsKey(authToken)) {
            return new ArrayList<>(games.values());
        }
        return null;
    }

    @Override
    public GameData createGame(String gameName, int gameID) {
        var game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(game.gameID(), game);
        return game;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

}
