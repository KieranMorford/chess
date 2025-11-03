package dataaccess;

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
            Assertions.assertEquals(user.username(), userR.username());
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to get user", ex);
        }
    }

    @Test
    void createUserNegative() {
    }

    @Test
    void getUserPositive() {
    }

    @Test
    void getUserNegative() {
    }

    @Test
    void createAuthPositive() {
    }

    @Test
    void createAuthNegative() {
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