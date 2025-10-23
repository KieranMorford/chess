package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public class SQLDataAccess implements DataAccess {
    public SQLDataAccess() throws DataAccessException{
        DatabaseManager.createDatabase();
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public GameData listGames(String authToken) {
        return null;
    }

    @Override
    public GameData createGame(String gameName, int gameID) {
        return null;
    }
}
