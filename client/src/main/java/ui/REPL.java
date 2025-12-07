package ui;


import client.Client;
import client.GameClient;
import client.LoggedInClient;
import client.LoggedOutClient;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;

import java.util.Scanner;

public class REPL {
    private final Client client;
    private final String serverUrl;
    private boolean quit = false;

    public REPL(Client client, String serverUrl) {
        this.client = client;
        this.serverUrl = serverUrl;
    }

    public void printToConsole(String message) {
        var serializer = new Gson();
        System.out.println(message);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit") && !result.equals("You have Successfully been Logged Out!") && !result.equals("You left the game.")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            if (result != null && (result.equals("Registered Successfully! You are now Logged in!") || result.equals("Logged In Successfully!"))) {
                LoggedInClient lIClient = null;
                try {
                    lIClient = new LoggedInClient(serverUrl, client.getAuthToken());
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
                REPL nRepl = new REPL(lIClient, serverUrl);
                nRepl.run();
                if (nRepl.checkQuit()) {
                    result = "quit";
                }
            }
            if (result != null && client.getClass() != GameClient.class && result.length() > 300 && !result.startsWith("Game ID:") && !result.startsWith("[38", 1)) {
                GameClient gClient = null;
                try {
                    gClient = new GameClient(this, serverUrl, client.getAuthToken(), client.getGame(), client.getId(), client.getColor());
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
                REPL nRepl = new REPL(gClient, serverUrl);
                nRepl.run();
            }
        }
        if (result.equals("quit")) {
            quit = true;
        }
        System.out.println();
    }

    private void printPrompt() {
        String state = null;
        if (client.getClass() == LoggedOutClient.class) {
            state = "[LOGGED_OUT]";
        } else if (client.getClass() == LoggedInClient.class) {
            state = "[LOGGED_IN]";
        } else if (client.getClass() == GameClient.class) {
            state = "[GAME]";
        }
        System.out.print("\n" + state + " >>> ");
    }

    private boolean checkQuit() {
        return quit;
    }
}
