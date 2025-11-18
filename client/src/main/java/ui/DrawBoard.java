package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class DrawBoard {
    public static String render(ChessBoard board) {
        var sb = new StringBuilder();
        sb.append(ERASE_SCREEN);
        sb.append(SET_BG_COLOR_LIGHT_GREY);
        sb.append(SET_TEXT_COLOR_RED);
        sb.append("     a    b    c    d    e    f    g    h\n");
        for (int row = 8; row >= 1; row--) {
            sb.append(SET_TEXT_COLOR_RED);
            sb.append(SET_BG_COLOR_LIGHT_GREY);
            sb.append(" ").append(row).append(" ");
            for (int col = 1; col <= 8; col++) {
                if ((row + col) % 2 == 0) {
                    sb.append(SET_TEXT_COLOR_BLACK);
                    sb.append(SET_BG_COLOR_GREEN);
                } else {
                    sb.append(SET_TEXT_COLOR_BLACK);
                    sb.append(SET_BG_COLOR_DARK_GREEN);
                }
                var piece = board.getPiece(new ChessPosition(row, col));
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
                sb.append(" ");
            }
            sb.append(SET_TEXT_COLOR_RED);
            sb.append(SET_BG_COLOR_LIGHT_GREY);
            sb.append(" ").append(row).append(" ");
            sb.append("\n");
        }
        sb.append(SET_BG_COLOR_LIGHT_GREY);
        sb.append(SET_TEXT_COLOR_RED);
        sb.append("     a    b    c    d    e    f    g    h\n");
        sb.append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);

        return sb.toString();
    }
}
