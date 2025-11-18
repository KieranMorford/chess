package dataaccess;

import chess.ChessGame;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLDataAccessTest {

    private SQLDataAccess dA = new SQLDataAccess();

    SQLDataAccessTest() throws DataAccessException {
    }

    @Test
    void clear() throws DataAccessException {
        dA.clear();
        var user = new UserData("link", "kronos", "kcmorford@gmail.com");
        dA.createUser(user);
        var auth = new AuthData("link", "Token");
        dA.createAuth(auth);
        dA.createGame("First Strand-type Game", 123);
        dA.clear();
        assertNull(dA.getUser("link"));
        assertThrows(UnauthorizedException.class, () -> {dA.getAuth("Token");});
        assertThrows(BadRequestException.class, () -> {dA.getGame(123);});
    }

    @Test
    void createUserPositive() throws DataAccessException {
        var user = new UserData("link", "kronos", "kcmorford@gmail.com");
        UserData userR = null;
        dA.clear();
        dA.createUser(user);
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
        dA.clear();
        assertThrows(DataAccessException.class, () -> {dA.createUser(user);});
    }

    @Test
    void getUserPositive() throws DataAccessException {
        var user = new UserData("link", "kronos", "kcmorford@gmail.com");
        dA.clear();
        dA.createUser(user);
        UserData userR = dA.getUser(user.username());
        assertEquals("link", userR.username());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        dA.clear();
        assertThrows(DataAccessException.class, () -> {dA.getUser(null);});
    }

    @Test
    void createAuthPositive() throws DataAccessException, UnauthorizedException {
        var auth = new AuthData("link", "Token");
        dA.clear();
        dA.createAuth(auth);
        assertEquals("link", dA.getAuth("Token").username());
    }

    @Test
    void createAuthNegative() throws DataAccessException {
        var auth = new AuthData("link", "Token");
        dA.clear();
        dA.createAuth(auth);
        assertThrows(UnauthorizedException.class, () -> {dA.getAuth("BadToken");});
    }

    @Test
    void getAuthPositive() throws DataAccessException, UnauthorizedException {
        var auth1 = new AuthData("link", "Token");
        var auth2 = new AuthData("zelda", "shinyToken");
        dA.clear();
        dA.createAuth(auth1);
        dA.createAuth(auth2);
        assertEquals("link", dA.getAuth("Token").username());
        assertEquals("zelda", dA.getAuth("shinyToken").username());
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        var auth = new AuthData("link", "Token");
        dA.clear();
        dA.createAuth(auth);
        assertThrows(UnauthorizedException.class, () -> {dA.getAuth(null);});
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        var auth = new AuthData("link", "Token");
        dA.clear();
        dA.createAuth(auth);
        assertDoesNotThrow(() -> {dA.deleteAuth("Token");});
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        var auth = new AuthData("link", "Token");
        dA.clear();
        dA.createAuth(auth);
        assertDoesNotThrow(() -> {dA.deleteAuth("Token");});
        assertThrows(UnauthorizedException.class, () -> {dA.deleteAuth("Token");});
    }

    @Test
    void listGamesPositive() throws DataAccessException, UnauthorizedException {
        var user = new AuthData("link", "token");
        dA.clear();
        dA.createAuth(user);
        dA.createGame("First Strand-type Game", 123);
        dA.createGame("Second Strand-type Game", 456);
        dA.createGame("First Woman-With-a-Sword-type Game", 789);
        var list = dA.listGames(user.authToken());
        assertEquals(3, list.size());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        dA.clear();
        assertThrows(UnauthorizedException.class, () -> {dA.listGames("Token");});
    }

    @Test
    void createGamePositive() throws DataAccessException {
        dA.clear();
        assertDoesNotThrow(() -> {dA.createGame("First Strand-type Game", 123);});
    }

    @Test
    void createGameNegative() throws DataAccessException {
        dA.clear();
        dA.createGame("First Strand-type Game", 123);
        assertThrows(DataAccessException.class, () -> {dA.createGame("First Strand-type Game", 123);});
    }

    @Test
    void getGamePositive() throws DataAccessException, BadRequestException {
        dA.clear();
        dA.createGame("First Strand-type Game", 123);
        assertEquals("First Strand-type Game", dA.getGame(123).gameName());
    }

    @Test
    void getGameNegative() throws DataAccessException {
        dA.clear();
        dA.createGame("First Strand-type Game", 123);
        assertThrows(BadRequestException.class, () -> {dA.getGame(1);});
    }

    @Test
    void updateGamePositive() throws DataAccessException, BadRequestException {
        dA.clear();
        dA.createGame("First Strand-type Game", 123);
        var gameData = dA.getGame(123);
        dA.updateGame(new GameData(123, "CrackerJack", "Epaminondas", "First Strand-type Game", gameData.game()));
        assertEquals("CrackerJack", dA.getGame(123).whiteUsername());
    }

    @Test
    void updateGameNegative() throws DataAccessException {
        dA.clear();
        assertThrows(BadRequestException.class, () ->
        {dA.updateGame(new GameData(123, "CrackerJack", "Epaminondas", "First Strand-type Game", new ChessGame()));});
    }
}