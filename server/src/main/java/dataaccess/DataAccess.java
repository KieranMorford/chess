package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    void createGame(String gameName);
    void createAuth(AuthData authData);
}
