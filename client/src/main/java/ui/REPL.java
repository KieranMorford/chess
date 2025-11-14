package ui;


import client.Client;
import client.LoggedInClient;
import client.LoggedOutClient;

import java.util.Scanner;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static jdk.javadoc.internal.doclets.formats.html.markup.HtmlAttr.InputType.RESET;

public class REPL {
    private final Client client;
    private final String serverUrl;
    private boolean quit = false;

    public REPL(Client client, String serverUrl) {
        this.client = client;
        this.serverUrl = serverUrl;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit") && !result.equals("You have Successfully been Logged Out!")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            if (result.equals("Registered Successfully! You are now Logged in!") || result.equals("Logged In Successfully!")) {
                LoggedInClient lIClient = new LoggedInClient(serverUrl);
                REPL nRepl = new REPL(lIClient, serverUrl);
                nRepl.run();
                if (nRepl.checkQuit()) {
                    result = "quit";
                }
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
        }
        System.out.print("\n" + state + " >>> ");
    }

    private boolean checkQuit() {
        return quit;
    }
}
