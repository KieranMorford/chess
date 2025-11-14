import client.LoggedOutClient;
import ui.REPL;

public class ClientMain {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        LoggedOutClient lOClient = new LoggedOutClient(serverUrl);
        System.out.println("♕ Welcome to the 240 Chess Client, type \"help\" to see a list of available commands. ♕");
        new REPL(lOClient, serverUrl).run();
    }
}