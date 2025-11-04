package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requestresult.LoginRequest;
import requestresult.NewGameRequest;
import requestresult.RegisterRequest;
import service.GameService;
import service.UserService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLDataAccessTest {

    private SQLDataAccess DA = new SQLDataAccess();

    SQLDataAccessTest() throws DataAccessException {
    }

    @Test
    void clear() throws DataAccessException {
        DA.clear();
        var user = new UserData("link", "kronos", "kcmorford@gmail.com");
        DA.createUser(user);
        var auth = new AuthData("link", "Token");
        DA.createAuth(auth);
        DA.createGame("First Strand-type Game", 123);
        DA.clear();
        assertNull(DA.getUser("link"));
        assertThrows(UnauthorizedException.class, () -> {DA.getAuth("Token");});
        assertThrows(BadRequestException.class, () -> {DA.getGame(123);});
    }

    @Test
    void createUserPositive() throws DataAccessException {
        var user = new UserData("link", "kronos", "kcmorford@gmail.com");
        UserData userR = null;
        DA.clear();
        DA.createUser(user);
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM UserData WHERE username = ?;");
            preparedStatement.setString(1, user.username());
            var userSet = preparedStatement.executeQuery();
            if (userSet.next()) {
                userR = new UserData(userSet.getString("username"), userSet.getString("password"), userSet.getString("email"));
            }
            assertEquals(user.username(), userR.username());
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to get user", ex);
        }
    }

    @Test
    void createUserNegative() throws DataAccessException {
        var user = new UserData(null, "kronos", "kcmorford@gmail.com");
        DA.clear();
        assertThrows(DataAccessException.class, () -> {DA.createUser(user);});
    }

    @Test
    void getUserPositive() throws DataAccessException {
        var user = new UserData("link", "kronos", "kcmorford@gmail.com");
        DA.clear();
        DA.createUser(user);
        UserData userR = DA.getUser(user.username());
        assertEquals("link", userR.username());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        DA.clear();
        assertThrows(DataAccessException.class, () -> {DA.getUser(null);});
    }

    @Test
    void createAuthPositive() throws DataAccessException, UnauthorizedException {
        var auth = new AuthData("link", "Token");
        DA.clear();
        DA.createAuth(auth);
        assertEquals("link", DA.getAuth("Token").username());
    }

    @Test
    void createAuthNegative() throws DataAccessException {
        var auth = new AuthData("link", "Token");
        DA.clear();
        DA.createAuth(auth);
        assertThrows(UnauthorizedException.class, () -> {DA.getAuth("BadToken");});
    }

    @Test
    void getAuthPositive() throws DataAccessException, UnauthorizedException {
        var auth = new AuthData("link", "Token");
        DA.clear();
        DA.createAuth(auth);
        assertEquals("link", DA.getAuth("Token").username());
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        var auth = new AuthData("link", "Token");
        DA.clear();
        DA.createAuth(auth);
        assertThrows(UnauthorizedException.class, () -> {DA.getAuth(null);});
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        var auth = new AuthData("link", "Token");
        DA.clear();
        DA.createAuth(auth);
        assertDoesNotThrow(() -> {DA.deleteAuth("Token");});
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        var auth = new AuthData("link", "Token");
        DA.clear();
        DA.createAuth(auth);
        assertDoesNotThrow(() -> {DA.deleteAuth("Token");});
        assertThrows(UnauthorizedException.class, () -> {DA.deleteAuth("Token");});
    }

    @Test
    void listGamesPositive() throws DataAccessException, UnauthorizedException {
        var user = new AuthData("link", "token");
        DA.clear();
        DA.createAuth(user);
        DA.createGame("First Strand-type Game", 123);
        DA.createGame("Second Strand-type Game", 456);
        DA.createGame("First Woman-With-a-Sword-type Game", 789);
        var list = DA.listGames(user.authToken());
        assertEquals(3, list.size());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        DA.clear();
        assertThrows(UnauthorizedException.class, () -> {DA.listGames("Token");});
    }

    @Test
    void createGamePositive() throws DataAccessException {
        DA.clear();
        assertDoesNotThrow(() -> {DA.createGame("First Strand-type Game", 123);});
    }

    @Test
    void createGameNegative() throws DataAccessException {
        DA.clear();
        DA.createGame("First Strand-type Game", 123);
        assertThrows(DataAccessException.class, () -> {DA.createGame("First Strand-type Game", 123);});
    }

    @Test
    void getGamePositive() throws DataAccessException, BadRequestException {
        DA.clear();
        DA.createGame("First Strand-type Game", 123);
        assertEquals("First Strand-type Game", DA.getGame(123).gameName());
    }

    @Test
    void getGameNegative() throws DataAccessException {
        DA.clear();
        DA.createGame("First Strand-type Game", 123);
        assertThrows(BadRequestException.class, () -> {DA.getGame(1);});
    }

    @Test
    void updateGamePositive() throws DataAccessException, BadRequestException {
        DA.clear();
        DA.createGame("First Strand-type Game", 123);
        var gameData = DA.getGame(123);
        DA.updateGame(new GameData(123, "CrackerJack", "Epaminondas", "First Strand-type Game", gameData.game()));
        assertEquals("CrackerJack", DA.getGame(123).whiteUsername());
    }

    @Test
    void updateGameNegative() throws DataAccessException {
        DA.clear();
        assertThrows(BadRequestException.class, () -> {DA.updateGame(new GameData(123, "CrackerJack", "Epaminondas", "First Strand-type Game", new ChessGame()));});
    }
}