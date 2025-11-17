package client;

import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import java.io.IOException;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPositiveTest() {

    }

    @Test
    public void registerNegativeTest() {
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
