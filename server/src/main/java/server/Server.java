package server;

import RequestResult.RegisterRequest;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        userService = new UserService(new MemoryDataAccess());
        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var regReq = serializer.fromJson(reqJson, RegisterRequest.class);

            var registerResult = userService.register(regReq);

            ctx.result(serializer.toJson(registerResult));
        } catch (Exception ex) {
            var msg = String.format("Error: %s", ex.getMessage());
            ctx.status(403).result(msg);
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
