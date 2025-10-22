package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    void createGame(String gameName);
//    GameData getGame(AuthData)
}
