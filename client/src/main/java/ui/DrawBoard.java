package ui;

import chess.*;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static ui.EscapeSequences.*;

public class DrawBoard {

    public static String render(ChessGame game, ChessGame.TeamColor teamColor, ChessPosition position) {
        var board = game.getBoard();
        var sb = new StringBuilder();
        sb.append(ERASE_SCREEN);
        header(teamColor, sb);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            bodyWhite(game, board, sb, position);
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            bodyBlack(game, board, sb, position);
        }
        header(teamColor, sb);
        sb.append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);

        return sb.toString();
    }

    private static void bodyWhite(ChessGame game, ChessBoard board, StringBuilder sb, ChessPosition position) {
        for (int row = 8; row >= 1; row--) {
            sb.append(SET_TEXT_COLOR_RED);
            sb.append(SET_BG_COLOR_DARK_GREY);
            sb.append(" ").append(row).append(" ");
            for (int col = 1; col <= 8; col++) {
                pieces(game, board, sb, row, col, position);
                sb.append(" ");
            }
            sb.append(SET_TEXT_COLOR_RED);
            sb.append(SET_BG_COLOR_DARK_GREY);
            sb.append(" ").append(row).append(" ");
            sb.append(RESET_BG_COLOR).append("\n");
        }
    }

    private static void bodyBlack(ChessGame game, ChessBoard board, StringBuilder sb, ChessPosition position) {
        for (int row = 1; row <= 8; row++) {
            sb.append(SET_TEXT_COLOR_RED);
            sb.append(SET_BG_COLOR_DARK_GREY);
            sb.append(" ").append(row).append(" ");
            for (int col = 8; col >= 1; col--) {
                pieces(game, board, sb, row, col, position);
                sb.append(" ");
            }
            sb.append(SET_TEXT_COLOR_RED);
            sb.append(SET_BG_COLOR_DARK_GREY);
            sb.append(" ").append(row).append(" ");
            sb.append(RESET_BG_COLOR).append("\n");
        }
    }

    private static void pieces(ChessGame game, ChessBoard board, StringBuilder sb, int row, int col, ChessPosition position) {
        Collection<ChessMove> moves = null;
        var pos = new ChessPosition(row, col);
        AtomicBoolean highlight = new AtomicBoolean(false);
        if (position != null) {
            if (position.equals(pos)) {
                sb.append(SET_TEXT_COLOR_BLACK);
                sb.append(SET_BG_COLOR_BLUE);
                highlight.set(true);
            }
            if (game.getBoard().getPiece(position) != null) {
                moves = game.validMoves(position);
//                moves = board.getPiece(position).pieceMoves(board, position);
            }
            if (moves != null) {
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(pos) && (row + col) % 2 == 0) {
                        highlight.set(true);
                        sb.append(SET_TEXT_COLOR_BLACK);
                        sb.append(SET_BG_COLOR_DARK_GREEN);
                    } else if (move.getEndPosition().equals(pos) && (row + col) % 2 == 1){
                        highlight.set(true);
                        sb.append(SET_BG_COLOR_GREEN);
                        sb.append(SET_BG_COLOR_GREEN);
                    }
                }
            }
        }
        if ((row + col) % 2 == 0 && !highlight.get()) {
            sb.append(SET_TEXT_COLOR_BLACK);
            sb.append(SET_BG_COLOR_LIGHT_GREY);
        } else if (!highlight.get()) {
            sb.append(SET_TEXT_COLOR_BLACK);
            sb.append(SET_BG_COLOR_WHITE);
        }
        var piece = game.getBoard().getPiece(pos);
        if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                sb.append(" ").append(WHITE_PAWN);
            } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                sb.append(" ").append(WHITE_KNIGHT);
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                sb.append(" ").append(WHITE_ROOK);
            } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                sb.append(" ").append(WHITE_BISHOP);
            } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                sb.append(" ").append(WHITE_QUEEN);
            } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                sb.append(" ").append(WHITE_KING);
            }
        } else if (piece != null && piece.getTeamColor() ==  ChessGame.TeamColor.BLACK) {
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                sb.append(" ").append(BLACK_PAWN);
            } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                sb.append(" ").append(BLACK_KNIGHT);
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                sb.append(" ").append(BLACK_ROOK);
            } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                sb.append(" ").append(BLACK_BISHOP);
            } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                sb.append(" ").append(BLACK_QUEEN);
            } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                sb.append(" ").append(BLACK_KING);
            }
        } else {
            sb.append(" ").append(EMPTY);
        }
        highlight.set(false);
    }

    private static void header(ChessGame.TeamColor teamColor, StringBuilder sb) {
        sb.append(SET_BG_COLOR_DARK_GREY);
        sb.append(SET_TEXT_COLOR_RED);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            sb.append("     a    b    c    d    e    f    g    h     ").append(RESET_BG_COLOR).append("\n");
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            sb.append("     h    g    f    e    d    c    b    a     ").append(RESET_BG_COLOR).append("\n");
        }
    }
}
