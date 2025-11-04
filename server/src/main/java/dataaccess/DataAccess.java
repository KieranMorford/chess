package dataaccess;

import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException, UnauthorizedException;
    void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException;
    List<GameData> listGames(String authToken);
    GameData createGame(String gameName, int gameID) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException, BadRequestException;
    void updateGame(GameData gameData);
}
