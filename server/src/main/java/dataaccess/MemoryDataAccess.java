package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> userAuth = new HashMap<>();
    private final HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        userAuth.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createGame(String gameName) {

    }

    @Override
    public void createAuth(AuthData authData) {
        userAuth.put(authData.username(), authData);
    }
}
