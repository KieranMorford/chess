package service;

import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import requestresult.LoginRequest;
import requestresult.LogoutRequest;
import requestresult.NewGameRequest;
import requestresult.RegisterRequest;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerPositiveTest() throws AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(new MemoryDataAccess());
        var regRes = userService.register(regReq);
        assertEquals("link", regRes.username());
        assertNotNull(regRes.authToken());
    }

    @Test
    void registerRepeatTest() throws AlreadyTakenException, BadRequestException {
        RegisterRequest regReq1 = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        RegisterRequest regReq2 = new RegisterRequest("link","zelda","kord@gmail.com");
        UserService userService = new UserService(new MemoryDataAccess());
        userService.register(regReq1);
        assertThrows(AlreadyTakenException.class, () -> {userService.register(regReq2);});
    }

    @Test
    void registerBadRequestTest() throws AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos",null);
        UserService userService = new UserService(new MemoryDataAccess());
        assertThrows(BadRequestException.class, () -> {userService.register(regReq);});
    }

    @Test
    void loginPositiveTest() throws UnauthorizedException, AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(new MemoryDataAccess());
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        assertEquals("link", logRes.username());
    }

    @Test
    void loginUnauthorizedTest() throws UnauthorizedException, AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(new MemoryDataAccess());
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","krony");
        assertThrows(UnauthorizedException.class, () -> {userService.login(logReq);});
    }

    @Test
    void loginBadRequestTest() throws UnauthorizedException, AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        UserService userService = new UserService(new MemoryDataAccess());
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest(null,"kronos");
        assertThrows(BadRequestException.class, () -> {userService.login(logReq);});
    }

    @Test
    void logoutPositiveTest() throws UnauthorizedException, AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        var mDA = new MemoryDataAccess();
        UserService userService = new UserService(mDA);
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        LogoutRequest logoReq = new LogoutRequest(logRes.authToken());
        assertDoesNotThrow(() -> {userService.logout(logoReq);});
    }

    @Test
    void logoutUnauthorizedTest() throws UnauthorizedException, AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        var mDA = new MemoryDataAccess();
        UserService userService = new UserService(mDA);
        userService.register(regReq);
        LogoutRequest logoReq = new LogoutRequest("notAuthToken");
        assertThrows(UnauthorizedException.class, () -> {userService.logout(logoReq);});
    }

    @Test
    void clear() throws UnauthorizedException, AlreadyTakenException, BadRequestException {
        RegisterRequest regReq = new RegisterRequest("link","kronos","kcmorford@gmail.com");
        var mDA = new MemoryDataAccess();
        UserService userService = new UserService(mDA);
        GameService gameService = new GameService(mDA);
        userService.register(regReq);
        LoginRequest logReq = new LoginRequest("link","kronos");
        var logRes = userService.login(logReq);
        NewGameRequest nGReq1 = new NewGameRequest(logRes.authToken(), "First Strand-type Game");
        NewGameRequest nGReq2 = new NewGameRequest(logRes.authToken(), "Second Strand-type Game");
        NewGameRequest nGReq3 = new NewGameRequest(logRes.authToken(), "First Woman-With-a-Sword-type Game");
        gameService.newGame(nGReq1);
        gameService.newGame(nGReq2);
        gameService.newGame(nGReq3);
        userService.clear();
        assertThrows(UnauthorizedException.class, () -> {gameService.getGameList(logRes.authToken()).games().size();});
    }
}