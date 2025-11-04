package dataaccess;

import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
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
    void clear() {
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
        UserData userR = null;
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
    void createAuthNegative() throws DataAccessException, UnauthorizedException {
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
    void listGamesPositive() {
    }

    @Test
    void listGamesNegative() {
    }

    @Test
    void createGamePositive() throws DataAccessException {
        var DA = new SQLDataAccess();
        DA.clear();
        assertDoesNotThrow(() -> {DA.createGame("First Strand-type Game", 123);});
    }

    @Test
    void createGameNegative() throws DataAccessException {
        var DA = new SQLDataAccess();
        DA.clear();
        DA.createGame("First Strand-type Game", 123);
        assertThrows(DataAccessException.class, () -> {DA.createGame("First Strand-type Game", 123);});
    }

    @Test
    void getGamePositive() throws DataAccessException, BadRequestException {
        var DA = new SQLDataAccess();
        DA.clear();
        DA.createGame("First Strand-type Game", 123);
        assertEquals("First Strand-type Game", DA.getGame(123).gameName());
    }

    @Test
    void getGameNegative() throws DataAccessException, BadRequestException {
        var DA = new SQLDataAccess();
        DA.clear();
        DA.createGame("First Strand-type Game", 123);
        assertThrows(BadRequestException.class, () -> {DA.getGame(1);});
    }

    @Test
    void updateGamePositive() {
    }

    @Test
    void updateGameNegative() {
    }
}