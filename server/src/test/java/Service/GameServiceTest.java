package Service;

import Exceptions.AlreadyTakenException;
import Exceptions.BadRequestException;
import Exceptions.UnauthorizedException;
import RequestResult.LoginRequest;
import RequestResult.NewGameRequest;
import RequestResult.RegisterRequest;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void newGamePositiveTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        var mDA = new MemoryDataAccess();
        UserService userService = new UserService(mDA);
        GameService gameService = new GameService(mDA);
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
    void newGameBadRequestTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        var mDA = new MemoryDataAccess();
        UserService userService = new UserService(mDA);
        GameService gameService = new GameService(mDA);
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq1 = new NewGameRequest(null, "First Strand-type Game");
        NewGameRequest nGReq2 = new NewGameRequest(logRes.authToken(), null);
        assertThrows(BadRequestException.class, () -> {gameService.newGame(nGReq1);});
        assertThrows(BadRequestException.class, () -> {gameService.newGame(nGReq2);});
    }

    @Test
    void newGameUnauthorizedTest() throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        var mDA = new MemoryDataAccess();
        UserService userService = new UserService(mDA);
        GameService gameService = new GameService(mDA);
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq = new NewGameRequest("BadAuthToken", "First Strand-type Game");
        assertThrows(UnauthorizedException.class, () -> {gameService.newGame(nGReq);});
    }


}