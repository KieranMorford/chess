package server;

import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import requestresult.*;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final MemoryDataAccess memoryDataAccess;
    private final UserService userService;
    private final GameService gameService;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::getGameList)
                .post("/game", this::newGame)
                .put("/game", this::joinGame)
                .delete("/db", this::delete);
        memoryDataAccess = new MemoryDataAccess();
        userService = new UserService(memoryDataAccess);
        gameService = new GameService(memoryDataAccess);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var regReq = serializer.fromJson(reqJson, RegisterRequest.class);

            var registerResult = userService.register(regReq);

            ctx.result(serializer.toJson(registerResult));
        } catch (BadRequestException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(400).result(serializer.toJson(exJson));
        } catch (AlreadyTakenException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(403).result(serializer.toJson(exJson));
        }
    }

    private void login(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var logReq = serializer.fromJson(reqJson, LoginRequest.class);

            var loginResult = userService.login(logReq);

            ctx.result(serializer.toJson(loginResult));
        } catch (BadRequestException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(400).result(serializer.toJson(exJson));
        } catch (UnauthorizedException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(401).result(serializer.toJson(exJson));
        }
    }

    private void logout(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.header("authorization");
            var logoReq = new LogoutRequest(reqJson);

            userService.logout(logoReq);

            ctx.result(serializer.toJson(null));
        } catch (UnauthorizedException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(401).result(serializer.toJson(exJson));
        }
    }

    private void getGameList(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("authorization");

            var gGLRes = gameService.getGameList(authToken);

            ctx.result(serializer.toJson(gGLRes));
        } catch (UnauthorizedException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(401).result(serializer.toJson(exJson));
        }
    }

    private void newGame(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("authorization");
            var reqJson = serializer.fromJson(ctx.body(), NewGameRequest.class);
            var nGReq = new NewGameRequest(authToken, reqJson.gameName());

            var newGameResult = gameService.newGame(nGReq);

            ctx.result(serializer.toJson(newGameResult));
        } catch (BadRequestException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(400).result(serializer.toJson(exJson));
        } catch (UnauthorizedException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(401).result(serializer.toJson(exJson));
        }
    }

    private void joinGame(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("authorization");
            String reqJson = ctx.body();
            var preJGReq = serializer.fromJson(reqJson, PreJoinRequest.class);
            if (preJGReq.gameID() == null || preJGReq.playerColor() == null) {
                throw new BadRequestException("Bad Request");
            }
            var jGReq = new JoinGameRequest(null, null,0);
            if (preJGReq.playerColor().equals("WHITE")) {
                jGReq = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, Integer.parseInt(preJGReq.gameID()));
            } else if (preJGReq.playerColor().equals("BLACK")) {
                jGReq = new JoinGameRequest(authToken, ChessGame.TeamColor.BLACK, Integer.parseInt(preJGReq.gameID()));
            }

            gameService.joinGame(jGReq);

            ctx.result(serializer.toJson(null));
        } catch (BadRequestException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(400).result(serializer.toJson(exJson));
        } catch (UnauthorizedException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(401).result(serializer.toJson(exJson));
        } catch (AlreadyTakenException ex) {
            Map<String, String> exJson = new HashMap<>();
            exJson.put("message", "Error: " + ex.getMessage());
            ctx.status(403).result(serializer.toJson(exJson));
        }
    }

    private void delete(Context ctx) {
        userService.clear();
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
