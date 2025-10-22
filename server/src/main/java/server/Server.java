package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.UserData;
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
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = userService.register(user);

            ctx.result(serializer.toJson(authData));
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
