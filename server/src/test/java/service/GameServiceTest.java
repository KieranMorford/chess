package service;

import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import requestresult.JoinGameRequest;
import requestresult.LoginRequest;
import requestresult.NewGameRequest;
import requestresult.RegisterRequest;
import chess.ChessGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

     private final SQLDataAccess dA = new SQLDataAccess();

    GameServiceTest() throws DataAccessException {
    }

    @Test
    void newGamePositiveTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq = new NewGameRequest(logRes.authToken(), "First Strand-type Game");
        var nGRes1 = gameService.newGame(nGReq);
        assertEquals(1,nGRes1.gameID());
        var nGRes2 = gameService.newGame(nGReq);
        assertEquals(2,nGRes2.gameID());
    }

    @Test
    void newGameBadRequestTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq1 = new NewGameRequest(null, "First Strand-type Game");
        NewGameRequest nGReq2 = new NewGameRequest(logRes.authToken(), null);
        assertThrows(BadRequestException.class, () -> {gameService.newGame(nGReq1);});
        assertThrows(BadRequestException.class, () -> {gameService.newGame(nGReq2);});
    }

    @Test
    void newGameUnauthorizedTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        userService.login(logReq);
        NewGameRequest nGReq = new NewGameRequest("BadAuthToken", "First Strand-type Game");
        assertThrows(UnauthorizedException.class, () -> {gameService.newGame(nGReq);});
    }

    @Test
    void joinGamePositive() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq = new NewGameRequest(logRes.authToken(), "First Strand-type Game");
        gameService.newGame(nGReq);
        var jGReq = new JoinGameRequest(logRes.authToken(), ChessGame.TeamColor.WHITE, 1);
        gameService.joinGame(jGReq);
        assertEquals("link", gameService.getGameList(logRes.authToken()).games().get(jGReq.gameID() - 1).whiteUsername());
    }

    @Test
    void joinGameBadRequestTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq = new NewGameRequest(logRes.authToken(), "First Strand-type Game");
        gameService.newGame(nGReq);
        var jGReq1 = new JoinGameRequest(logRes.authToken(), ChessGame.TeamColor.WHITE, 5);
        assertThrows(BadRequestException.class, () -> {gameService.joinGame(jGReq1);});
    }

    @Test
    void joinGameUnauthorizedTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq = new NewGameRequest(logRes.authToken(), "First Strand-type Game");
        gameService.newGame(nGReq);
        var jGReq = new JoinGameRequest("BadAuthToken", ChessGame.TeamColor.WHITE, 1);
        assertThrows(UnauthorizedException.class, () -> {gameService.joinGame(jGReq);});
    }

    @Test
    void joinGameAlreadyTakenTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest regReq1 = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        RegisterRequest regReq2 = new RegisterRequest("zelda","flash","hrmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq1);
        userService.register(regReq2);
        LoginRequest logReq1 = new LoginRequest("link","kronos");
        LoginRequest logReq2 = new LoginRequest("zelda","flash");
        var logRes1 = userService.login(logReq1);
        var logRes2 = userService.login(logReq2);
        NewGameRequest nGReq = new NewGameRequest(logRes1.authToken(), "First Strand-type Game");
        gameService.newGame(nGReq);
        var jGReq1 = new JoinGameRequest(logRes1.authToken(), ChessGame.TeamColor.WHITE, 1);
        var jGReq2 = new JoinGameRequest(logRes2.authToken(), ChessGame.TeamColor.WHITE, 1);
        gameService.joinGame(jGReq1);
        assertThrows(AlreadyTakenException.class, () -> {gameService.joinGame(jGReq2);});
    }

    @Test
    void listGamesPositiveTest() throws UnauthorizedException, AlreadyTakenException, BadRequestException, DataAccessException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq1 = new NewGameRequest(logRes.authToken(), "First Strand-type Game");
        NewGameRequest nGReq2 = new NewGameRequest(logRes.authToken(), "Second Strand-type Game");
        NewGameRequest nGReq3 = new NewGameRequest(logRes.authToken(), "First Woman-With-a-Sword-type Game");
        gameService.newGame(nGReq1);
        gameService.newGame(nGReq2);
        gameService.newGame(nGReq3);
        var listTest = gameService.getGameList(logRes.authToken());
        assertEquals(3, listTest.games().size());
    }

    @Test
    void listGamesUnauthorizedTest() throws UnauthorizedException, AlreadyTakenException, BadRequestException, DataAccessException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(dA);
        GameService gameService = new GameService(dA);
        userService.clear();
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq1 = new NewGameRequest(logRes.authToken(), "First Strand-type Game");
        gameService.newGame(nGReq1);
        assertThrows(UnauthorizedException.class, () -> {gameService.getGameList("BadAuthToken");});
    }
}