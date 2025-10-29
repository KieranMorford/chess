package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        ChessPiece piece = board.getPiece(myPos);
        var moves = new HashSet<ChessMove>();
        int x = 1;
        if (piece.getPieceType() == PieceType.BISHOP) {
            while (myPos.getRow() + x <= 8 && myPos.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() + x <= 8 && myPos.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() - x > 0 && myPos.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() - x > 0 && myPos.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
        }
        else if (piece.getPieceType() == PieceType.ROOK) {
            while (myPos.getRow() + x <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn())) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn())).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + x,myPos.getColumn()), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn())) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn())).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow(),myPos.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() - x > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn())) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn())).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - x,myPos.getColumn()), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn())) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn())).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow(),myPos.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
        }
        else if (piece.getPieceType() == PieceType.KING) {
            if (myPos.getRow() + 1 <= 8 && myPos.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() + 1, myPos.getColumn() + 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + 1,myPos.getColumn() + 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1,myPos.getColumn() + 1), null));
            }
            if (myPos.getRow() + 1 <= 8 && myPos.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() + 1, myPos.getColumn() - 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + 1,myPos.getColumn() - 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1,myPos.getColumn() - 1), null));
            }
            if (myPos.getRow() - 1 > 0 && myPos.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() - 1, myPos.getColumn() + 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - 1,myPos.getColumn() + 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1,myPos.getColumn() + 1), null));
            }
            if (myPos.getRow() - 1 > 0 && myPos.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() - 1, myPos.getColumn() - 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - 1,myPos.getColumn() - 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1,myPos.getColumn() - 1), null));
            }
            if (myPos.getRow() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() + 1, myPos.getColumn())) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + 1,myPos.getColumn())).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1,myPos.getColumn()), null));
            }
            if (myPos.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow(), myPos.getColumn() + 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow(),myPos.getColumn() + 1), null));
            }
            if (myPos.getRow() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() - 1, myPos.getColumn())) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - 1,myPos.getColumn())).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1,myPos.getColumn()), null));
            }
            if (myPos.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow(), myPos.getColumn() - 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow(),myPos.getColumn() - 1), null));
            }
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            while (myPos.getRow() + x <= 8 && myPos.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() + x <= 8 && myPos.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() - x > 0 && myPos.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() - x > 0 && myPos.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() + x <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn())) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn())).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + x,myPos.getColumn()), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn())) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() + x,myPos.getColumn())).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getColumn() - x > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow(),myPos.getColumn() - x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() - x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getRow() - x > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn())) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn())).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - x,myPos.getColumn()), null));
                if (board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn())) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() - x,myPos.getColumn())).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
            x = 1;
            while (myPos.getColumn() + x <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + x)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow(),myPos.getColumn() + x), null));
                if (board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + x)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow(),myPos.getColumn() + x)).getTeamColor() != piece.getTeamColor()) {
                    break;
                }
                x++;
            }
        }
        else if (piece.getPieceType() == PieceType.KNIGHT) {
            if (myPos.getRow() + 1 <= 8 && myPos.getColumn() + 2 <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() + 1, myPos.getColumn() + 2)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + 1,myPos.getColumn() + 2)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1,myPos.getColumn() + 2), null));
            }
            if (myPos.getRow() + 1 <= 8 && myPos.getColumn() - 2 > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() + 1, myPos.getColumn() - 2)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + 1,myPos.getColumn() - 2)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1,myPos.getColumn() - 2), null));
            }
            if (myPos.getRow() - 1 > 0 && myPos.getColumn() + 2 <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() - 1, myPos.getColumn() + 2)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - 1,myPos.getColumn() + 2)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1,myPos.getColumn() + 2), null));
            }
            if (myPos.getRow() - 1 > 0 && myPos.getColumn() - 2 > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() - 1, myPos.getColumn() - 2)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - 1,myPos.getColumn() - 2)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1,myPos.getColumn() - 2), null));
            }
            if (myPos.getRow() + 2 <= 8 && myPos.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() + 2, myPos.getColumn() + 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + 2,myPos.getColumn() + 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 2,myPos.getColumn() + 1), null));
            }
            if (myPos.getRow() + 2 <= 8 && myPos.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() + 2, myPos.getColumn() - 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() + 2,myPos.getColumn() - 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 2,myPos.getColumn() - 1), null));
            }
            if (myPos.getRow() - 2 > 0 && myPos.getColumn() + 1 <= 8
                    && (board.getPiece(new ChessPosition(myPos.getRow() - 2, myPos.getColumn() + 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - 2,myPos.getColumn() + 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 2,myPos.getColumn() + 1), null));
            }
            if (myPos.getRow() - 2 > 0 && myPos.getColumn() - 1 > 0
                    && (board.getPiece(new ChessPosition(myPos.getRow() - 2, myPos.getColumn() - 1)) == null
                    || board.getPiece(new ChessPosition(myPos.getRow() - 2,myPos.getColumn() - 1)).getTeamColor() != piece.getTeamColor())) {
                moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 2,myPos.getColumn() - 1), null));
            }
        }
        else if (piece.getPieceType() == PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (myPos.getRow() + 1 <= 8 && board.getPiece(new ChessPosition(myPos.getRow() + 1, myPos.getColumn())) == null) {
                    if (myPos.getRow() + 1 == 8) {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn()), PieceType.BISHOP));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn()), PieceType.ROOK));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn()), PieceType.QUEEN));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn()), PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn()), null));
                        if (board.getPiece(new ChessPosition(myPos.getRow() + 2, myPos.getColumn())) == null
                                && myPos.getRow() == 2) {
                            moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 2, myPos.getColumn()), null));
                        }
                    }
                }
                if (myPos.getRow() + 1 <= 8 && myPos.getColumn() + 1 <= 8
                        && board.getPiece(new ChessPosition(myPos.getRow() + 1,myPos.getColumn() + 1)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() + 1, myPos.getColumn() + 1)).getTeamColor() != piece.getTeamColor()) {
                    if (myPos.getRow() + 1 == 8) {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() + 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() + 1), PieceType.ROOK));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() + 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() + 1), PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() + 1), null));
                    }
                }
                if (myPos.getRow() + 1 <= 8 && myPos.getColumn() - 1 > 0
                        && board.getPiece(new ChessPosition(myPos.getRow() + 1,myPos.getColumn() - 1)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() + 1, myPos.getColumn() - 1)).getTeamColor() != piece.getTeamColor()) {
                    if (myPos.getRow() + 1 == 8) {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() - 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() - 1), PieceType.ROOK));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() - 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() - 1), PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() + 1, myPos.getColumn() - 1), null));
                    }
                }
            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (myPos.getRow() - 1 > 0 && board.getPiece(new ChessPosition(myPos.getRow() - 1, myPos.getColumn())) == null) {
                    if (myPos.getRow() - 1 == 1) {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn()), PieceType.BISHOP));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn()), PieceType.ROOK));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn()), PieceType.QUEEN));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn()), PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn()), null));
                        if (board.getPiece(new ChessPosition(myPos.getRow() - 2, myPos.getColumn())) == null
                                && myPos.getRow() == 7) {
                            moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 2, myPos.getColumn()), null));
                        }
                    }
                }
                if (myPos.getRow() - 1 > 0 && myPos.getColumn() + 1 <= 8
                        && board.getPiece(new ChessPosition(myPos.getRow() - 1,myPos.getColumn() + 1)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() - 1, myPos.getColumn() + 1)).getTeamColor() != piece.getTeamColor()) {
                    if (myPos.getRow() - 1 == 1) {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() + 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() + 1), PieceType.ROOK));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() + 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() + 1), PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() + 1), null));
                    }
                }
                if (myPos.getRow() - 1 > 0 && myPos.getColumn() - 1 > 0
                        && board.getPiece(new ChessPosition(myPos.getRow() - 1,myPos.getColumn() - 1)) != null
                        && board.getPiece(new ChessPosition(myPos.getRow() - 1, myPos.getColumn() - 1)).getTeamColor() != piece.getTeamColor()) {
                    if (myPos.getRow() - 1 == 1) {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() - 1), PieceType.BISHOP));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() - 1), PieceType.ROOK));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() - 1), PieceType.QUEEN));
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() - 1), PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPos, new ChessPosition(myPos.getRow() - 1, myPos.getColumn() - 1), null));
                    }
                }
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        return "[" + pieceColor +
                ", " + type +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
