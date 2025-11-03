package dataaccess;

import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLDataAccessTest {

    @Test
    void clear() {
    }

    @Test
    void createUserPositive() throws DataAccessException {
        var DA = new SQLDataAccess();
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
        var DA = new SQLDataAccess();
        var user = new UserData(null, "kronos", "kcmorford@gmail.com");
        UserData userR = null;
        DA.clear();
        assertThrows(DataAccessException.class, () -> {DA.createUser(user);});
    }

    @Test
    void getUserPositive() throws DataAccessException {
        var DA = new SQLDataAccess();
        var user = new UserData("link", "kronos", "kcmorford@gmail.com");
        DA.clear();
        DA.createUser(user);
        UserData userR = DA.getUser(user.username());
        assertEquals("link", userR.username());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        var DA = new SQLDataAccess();
        DA.clear();
        assertThrows(DataAccessException.class, () -> {DA.getUser(null);});
    }

    @Test
    void createAuthPositive() throws DataAccessException, UnauthorizedException {
        var DA = new SQLDataAccess();
        var auth = new AuthData("link", "Token");
        DA.clear();
        DA.createAuth(auth);
        assertEquals("link", DA.getAuth("Token").username());
    }

    @Test
    void createAuthNegative() throws DataAccessException, UnauthorizedException {
        var DA = new SQLDataAccess();
        var auth = new AuthData("link", "Token");
        DA.clear();
        DA.createAuth(auth);
        assertThrows(UnauthorizedException.class, () -> {DA.getAuth("BadToken");});
    }

    @Test
    void getAuthPositive() {
    }

    @Test
    void getAuthNegative() {
    }

    @Test
    void deleteAuthPositive() {
    }

    @Test
    void deleteAuthNegative() {
    }

    @Test
    void listGamesPositive() {
    }

    @Test
    void listGamesNegative() {
    }

    @Test
    void createGamePositive() {
    }

    @Test
    void createGameNegative() {
    }

    @Test
    void getGamePositive() {
    }

    @Test
    void getGameNegative() {
    }

    @Test
    void updateGamePositive() {
    }

    @Test
    void updateGameNegative() {
    }
}