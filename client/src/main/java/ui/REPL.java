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

    public REPL(Client client, String serverUrl) {
        this.client = client;
        this.serverUrl = serverUrl;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            if (result.equals("Registered Successfully! You are now Logged in!")) {
                LoggedInClient lIClient = new LoggedInClient(serverUrl);
                new REPL()
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

}
