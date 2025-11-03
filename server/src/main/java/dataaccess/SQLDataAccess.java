package dataaccess;

import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

public class SQLDataAccess implements DataAccess {
    public SQLDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (int i = 0; i < createStatements.length; i++) {
                var preparedStatement = conn.prepareStatement(createStatements[i]);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create tables", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("TRUNCATE TABLE `UserData`");
            preparedStatement.executeUpdate();
            preparedStatement = conn.prepareStatement("TRUNCATE TABLE `GameData`");
            preparedStatement.executeUpdate();
            preparedStatement = conn.prepareStatement("TRUNCATE TABLE `AuthData`");
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to clear database", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("INSERT INTO UserData (username, password, email) VALUES (?,?,?);");
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to add user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM UserData WHERE username = ?;");
            preparedStatement.setString(1, username);
            var userSet = preparedStatement.executeQuery();
            if (userSet.next()) {
                return new UserData(userSet.getString("username"), userSet.getString("password"), userSet.getString("email"));
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to get user", ex);
        }
        return null;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("INSERT INTO AuthData (authToken, username) VALUES (?,?);");
            preparedStatement.setString(1, authData.authToken());
            preparedStatement.setString(2, authData.username());
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to add user", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, UnauthorizedException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM AuthData WHERE authToken = ?;");
            preparedStatement.setString(1, authToken);
            var authSet = preparedStatement.executeQuery();
            if (authSet.next()) {
                return new AuthData(authSet.getString("authToken"), authSet.getString("username"));
            } else  {
                throw new UnauthorizedException("Unauthorized");
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to get auth", ex);
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public List<GameData> listGames(String authToken) {
        return null;
    }

    @Override
    public GameData createGame(String gameName, int gameID) {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  UserData (
              `username` VARCHAR(256) NOT NULL,
              `password` VARCHAR(256) NOT NULL,
              `email` VARCHAR(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  GameData (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(256) NOT NULL,
              `blackUsername` VARCHAR(256) NOT NULL,
              `gameName` VARCHAR(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  AuthData (
              `authToken` VARCHAR(256) NOT NULL,
              `username` VARCHAR(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
