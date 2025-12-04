package client;

import serverfacade.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class GameClient implements Client{

    private final ServerFacade server;
    private final String authToken;


    public GameClient(String serverUrl, String authToken, ) {
        server = new ServerFacade(serverUrl);
        this.authToken = authToken;
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "board" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> forfeitGame();
                case "highlight" -> highlightMoves(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }



    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE + "- board" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - display the board" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- leave" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - leave the game" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- move <COLROW> <COLROW>" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - move a piece (ex: move b2 b4)" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- resign" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - forfeit the game" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- highlight <COLROW>" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - highlight the moves a piece can make (ex: highlight b2)" + RESET_TEXT_ITALIC + "\n"
                + SET_TEXT_COLOR_BLUE + "- help" + RESET_TEXT_COLOR
                + SET_TEXT_ITALIC + " - display possible commands" + RESET_TEXT_ITALIC;
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    public String redrawBoard() {
        return "";
    }

    public String leaveGame() {
        return "";
    }

    public String forfeitGame() {
        return "";
    }

    public String makeMove(String[] params) {
        return "";
    }

    public String highlightMoves(String[] params) {
        return "";
    }
}
