package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        } catch (SQLException ex) {
            throw new DataAccessException("failed to add user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("username is null");
        }
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
                return new AuthData(authSet.getString("username"), authSet.getString("authToken"));
            } else  {
                throw new UnauthorizedException("Unauthorized");
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to get auth", ex);
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        }
    }

    public AuthData getAuthByUser(String username) throws DataAccessException, UnauthorizedException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM AuthData WHERE username = ?;");
            preparedStatement.setString(1, username);
            var authSet = preparedStatement.executeQuery();
            if (authSet.next()) {
                return new AuthData(authSet.getString("username"), authSet.getString("authToken"));
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
    public void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("DELETE FROM AuthData WHERE authToken = ?;");
            preparedStatement.setString(1, authToken);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new UnauthorizedException("Token not found");
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to get auth", ex);
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException("Bad AuthToken");
        }
    }

    @Override
    public List<GameData> listGames(String authToken) throws DataAccessException, UnauthorizedException {
        var serializer = new Gson();
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM AuthData WHERE authToken = ?;");
            preparedStatement.setString(1, authToken);
            var authSet = preparedStatement.executeQuery();
            if (!authSet.next()) {
                throw new UnauthorizedException("Unauthorized");
            }
            preparedStatement = conn.prepareStatement("SELECT * FROM GameData;");
            var gameSet = preparedStatement.executeQuery();
            List<GameData> list = new ArrayList<>();
            while (gameSet.next()) {
                var gameJson = gameSet.getString("game");
                ChessGame chessGame = serializer.fromJson(gameJson, ChessGame.class);
                list.add(new GameData(gameSet.getInt("gameID"), gameSet.getString("whiteUsername"),
                        gameSet.getString("blackUsername"), gameSet.getString("gameName"), chessGame));
            }
            return list;
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to get auth", ex);
        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        }
    }

    @Override
    public GameData createGame(String gameName, int gameID) throws DataAccessException {
        var cGame = new ChessGame();
        var game = new GameData(gameID, null, null, gameName, cGame);
        var serializer = new Gson();
        String gameJson = serializer.toJson(cGame);
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(
                    "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?,?);");
            preparedStatement.setString(1, Integer.toString(gameID));
            preparedStatement.setString(2, null);
            preparedStatement.setString(3, null);
            preparedStatement.setString(4, gameName);
            preparedStatement.setString(5, gameJson);
            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to create game", ex);
        }
        return game;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, BadRequestException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM GameData WHERE gameID = ?;");
            preparedStatement.setString(1, Integer.toString(gameID));
            var gameSet = preparedStatement.executeQuery();
            if (gameSet.next()) {
                var serializer = new Gson();
                var gameJson = gameSet.getString("game");
                ChessGame chessGame = serializer.fromJson(gameJson, ChessGame.class);
                return new GameData(gameSet.getInt("gameID"), gameSet.getString("whiteUsername"),
                        gameSet.getString("blackUsername"), gameSet.getString("gameName"), chessGame);
            } else {
                throw new BadRequestException("No Game with given ID");
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to create game", ex);
        } catch (BadRequestException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException, BadRequestException {
        var serializer = new Gson();
        String gameJson = serializer.toJson(gameData.game());
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM GameData WHERE gameID = ?;");
            preparedStatement.setString(1, Integer.toString(gameData.gameID()));
            var gameSet = preparedStatement.executeQuery();
            if (!gameSet.next()) {
                throw new BadRequestException("No Game with given ID");
            }
            var preparedStatement1 = conn.prepareStatement("DELETE FROM GameData WHERE gameID = ?;");
            preparedStatement1.setInt(1, gameData.gameID());
            preparedStatement1.executeUpdate();
            var preparedStatement2 = conn.prepareStatement(
                    "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?,?);");
            preparedStatement2.setString(1, Integer.toString(gameData.gameID()));
            preparedStatement2.setString(2, gameData.whiteUsername());
            preparedStatement2.setString(3, gameData.blackUsername());
            preparedStatement2.setString(4, gameData.gameName());
            preparedStatement2.setString(5, gameJson);
            preparedStatement2.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("failed to update game", ex);
        } catch (BadRequestException ex) {
            throw new BadRequestException(ex.getMessage());
        }
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
              `gameID` INT NOT NULL,
              `whiteUsername` VARCHAR(256),
              `blackUsername` VARCHAR(256),
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
