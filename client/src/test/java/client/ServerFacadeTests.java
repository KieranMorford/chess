package client;

import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import exceptions.RequestException;
import exceptions.ResponseException;
import org.junit.jupiter.api.*;
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
        var regReq = new RegisterRequest("linktest", "Kronostest", "lk@gmail.com");
        assertEquals("linktest", sF.register(regReq).username());
    }

    @Test
    public void registerNegativeTest() throws Exception {
        SQLDataAccess dA = new SQLDataAccess();
        dA.clear();
        var sF = new ServerFacade(serverUrl);
        var regReq = new RegisterRequest("linktest", "Kronostest", null);
        assertThrows(ResponseException.class, () -> sF.register(regReq));
    }

    @Test
    public void loginPositiveTest() {
    }

    @Test
    public void loginNegativeTest() {
    }

    @Test
    public void logoutPositiveTest() {
    }

    @Test
    public void logoutNegativeTest() {
    }

    @Test
    public void createGamePositiveTest() {
    }

    @Test
    public void createGameNegativeTest() {
    }

    @Test
    public void listGamesPositiveTest() {
    }

    @Test
    public void listGamesNegativeTest() {
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
