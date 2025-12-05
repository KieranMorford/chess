package server;

import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import requestresult.*;
import service.GameService;
import service.UserService;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final MemoryDataAccess memoryDataAccess;
    private final SQLDataAccess sqlDataAccess;
    private final UserService userServiceM;
    private final UserService userServiceSQL;
    private final GameService gameServiceM;
    private final GameService gameServiceSQL;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::getGameList)
                .post("/game", this::newGame)
                .put("/game", this::joinGame)
                .get("/game/watch", this::watchGame)
                .delete("/db", this::delete)
                .ws("/ws", ws -> {
                    ws.onConnect(this::connect);
                    ws.onMessage(this::evalMessage);
                    ws.onClose(this::close);
                });
        memoryDataAccess = new MemoryDataAccess();
        try {
            sqlDataAccess = new SQLDataAccess();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        userServiceM = new UserService(memoryDataAccess);
        userServiceSQL = new UserService(sqlDataAccess);
        gameServiceM = new GameService(memoryDataAccess);
        gameServiceSQL = new GameService(sqlDataAccess);
    }

    private void register(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var regReq = serializer.fromJson(reqJson, RegisterRequest.class);

            var registerResult = userServiceSQL.register(regReq);

            ctx.result(serializer.toJson(registerResult));
        } catch (BadRequestException ex) {
            reportError(serializer, ctx, ex, 400);
        } catch (AlreadyTakenException ex) {
            reportError(serializer, ctx, ex, 403);
        } catch (DataAccessException ex) {
            reportError(serializer, ctx, ex, 500);
        }
    }

    private void login(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var logReq = serializer.fromJson(reqJson, LoginRequest.class);

            var loginResult = userServiceSQL.login(logReq);

            ctx.result(serializer.toJson(loginResult));
        } catch (BadRequestException ex) {
            reportError(serializer, ctx, ex, 400);
        } catch (UnauthorizedException ex) {
            reportError(serializer, ctx, ex, 401);
        } catch (DataAccessException ex) {
            reportError(serializer, ctx, ex, 500);
        }
    }

    private void logout(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.header("authorization");
            var logoReq = new LogoutRequest(reqJson);

            userServiceSQL.logout(logoReq);

            ctx.result(serializer.toJson(null));
        } catch (UnauthorizedException ex) {
            reportError(serializer, ctx, ex, 401);
        } catch (DataAccessException ex) {
            reportError(serializer, ctx, ex, 500);
        }
    }

    private void getGameList(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("authorization");

            var gGLRes = gameServiceSQL.getGameList(authToken);

            ctx.result(serializer.toJson(gGLRes));
        } catch (UnauthorizedException ex) {
            reportError(serializer, ctx, ex, 401);
        } catch (DataAccessException ex) {
            reportError(serializer, ctx, ex, 500);
        }
    }

    private void newGame(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("authorization");
            var reqJson = serializer.fromJson(ctx.body(), NewGameRequest.class);
            var nGReq = new NewGameRequest(authToken, reqJson.gameName());

            var newGameResult = gameServiceSQL.newGame(nGReq);

            ctx.result(serializer.toJson(newGameResult));
        } catch (BadRequestException ex) {
            reportError(serializer, ctx, ex, 400);
        } catch (UnauthorizedException ex) {
            reportError(serializer, ctx, ex, 401);
        } catch (DataAccessException ex) {
            reportError(serializer, ctx, ex, 500);
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

            gameServiceSQL.joinGame(jGReq);

            ctx.result(serializer.toJson(null));
        } catch (BadRequestException ex) {
            reportError(serializer, ctx, ex, 400);
        } catch (UnauthorizedException ex) {
            reportError(serializer, ctx, ex, 401);
        } catch (AlreadyTakenException ex) {
            reportError(serializer, ctx, ex, 403);
        } catch (DataAccessException ex) {
            reportError(serializer, ctx, ex, 500);
        }
    }

    private void watchGame(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("authorization");
            String reqJson = ctx.body();
            var preJGReq = serializer.fromJson(reqJson, PreJoinRequest.class);
            if (preJGReq.gameID() == null) {
                throw new BadRequestException("Bad Request");
            }
            var jGReq = new JoinGameRequest(authToken, null, Integer.parseInt(preJGReq.gameID()));

            gameServiceSQL.joinGame(jGReq);

            ctx.result(serializer.toJson(null));
        } catch (BadRequestException ex) {
            reportError(serializer, ctx, ex, 400);
        } catch (UnauthorizedException ex) {
            reportError(serializer, ctx, ex, 401);
        } catch (AlreadyTakenException ex) {
            reportError(serializer, ctx, ex, 403);
        } catch (DataAccessException ex) {
            reportError(serializer, ctx, ex, 500);
        }
    }

    private void reportError(Gson serializer, Context ctx, Exception ex, int statusCode) {
        Map<String, String> exJson = new HashMap<>();
        exJson.put("message", "Error: " + ex.getMessage());
        ctx.status(statusCode).result(serializer.toJson(exJson));
    }

    private void delete(Context ctx) throws DataAccessException {
        var serializer = new Gson();
        try {
            userServiceSQL.clear();
        } catch (DataAccessException ex) {
            reportError(serializer, ctx, ex, 500);
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
