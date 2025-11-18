package client;

import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import exceptions.RequestException;
import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import requestresult.LoginRequest;
import requestresult.LogoutRequest;
import requestresult.NewGameRequest;
import requestresult.RegisterRequest;
import server.Server;
import serverfacade.ServerFacade;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static String serverUrl = "http://localhost:8080";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPositiveTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        assertEquals("linktest", sF.register(regReq).username());
        dA.clear();
    }

    @Test
    public void registerNegativeTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", null);
        assertThrows(ResponseException.class, () -> sF.register(regReq));
        dA.clear();
    }

    @Test
    public void loginPositiveTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        sF.register(regReq);
        var lOReq = new LogoutRequest(dA.getAuthByUser("linktest").authToken());
        sF.logout(lOReq);
        var lIReq =  new LoginRequest("linktest", "kronostest");
        assertDoesNotThrow(() -> sF.login(lIReq));
        dA.clear();
    }

    @Test
    public void loginNegativeTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        sF.register(regReq);
        var lOReq = new LogoutRequest(dA.getAuthByUser("linktest").authToken());
        sF.logout(lOReq);
        var lIReq =  new LoginRequest("linktest", "bad");
        assertThrows(ResponseException.class, () -> sF.login(lIReq));
        dA.clear();
    }

    @Test
    public void logoutPositiveTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        sF.register(regReq);
        var lORequest = new LogoutRequest(dA.getAuthByUser("linktest").authToken());
        sF.logout(lORequest);
        assertThrows(ResponseException.class, () -> sF.logout(lORequest));
        dA.clear();
    }

    @Test
    public void logoutNegativeTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        sF.register(regReq);
        var lORequest = new LogoutRequest("bad");
        assertThrows(ResponseException.class, () -> sF.logout(lORequest));
        dA.clear();
    }

    @Test
    public void createGamePositiveTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        sF.register(regReq);
        var nGReq = new NewGameRequest(dA.getAuthByUser("linktest").authToken(), "testgame");
        sF.createGame(nGReq);
        assertEquals(nGReq.gameName(), dA.getGame(1).gameName());
        dA.clear();
    }

    @Test
    public void createGameNegativeTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        sF.register(regReq);
        var nGReq = new NewGameRequest(dA.getAuthByUser("linktest").authToken(), null);
        assertThrows(ResponseException.class, () ->  sF.createGame(nGReq));
        dA.clear();
    }

    @Test
    public void listGamesPositiveTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        sF.register(regReq);
        var nGReq = new NewGameRequest(dA.getAuthByUser("linktest").authToken(), "Towerstest");
        sF.createGame(nGReq);
        var list = sF.listGames(dA.getAuthByUser("linktest").authToken());
        assertEquals("Towerstest", list.games().getFirst().gameName());
        dA.clear();
    }

    @Test
    public void listGamesNegativeTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "kronostest", "lk@gmail.com");
        sF.register(regReq);
        var nGReq = new NewGameRequest(dA.getAuthByUser("linktest").authToken(), "Towerstest");
        sF.createGame(nGReq);
        assertThrows(ResponseException.class, () -> sF.listGames("bad"));
        dA.clear();
    }

    @Test
    public void playGamePositiveTest() {
    }

    @Test
    public void playGameNegativeTest() {
    }

    @Test
    public void observeGamePositiveTest() {
    }

    @Test
    public void ObserveGameNegativeTest() {
    }

}
