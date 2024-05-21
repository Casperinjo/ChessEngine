package src.ChessGame;
import src.PieceUtil.Piece;

public class Move {
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final boolean isCastlingMove;
    private final boolean isEnPassantMove;
    private final Piece promotionPiece;

    public Move(int startX, int startY, int endX, int endY, Piece movedPiece, Piece capturedPiece, boolean isCastlingMove, boolean isEnPassantMove, Piece promotionPiece) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isCastlingMove = isCastlingMove;
        this.isEnPassantMove = isEnPassantMove;
        this.promotionPiece = promotionPiece;
    }

    // Getter methods
    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isCastlingMove() {
        return isCastlingMove;
    }

    public boolean isEnPassantMove() {
        return isEnPassantMove;
    }

    public Piece getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        return "Move{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                ", movedPiece=" + movedPiece +
                ", capturedPiece=" + capturedPiece +
                ", isCastlingMove=" + isCastlingMove +
                ", isEnPassantMove=" + isEnPassantMove +
                ", promotionPiece=" + promotionPiece +
                '}';
    }
}
