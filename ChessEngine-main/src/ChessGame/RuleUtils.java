package src.ChessGame;

import src.PieceUtil.King;
import src.PieceUtil.Pawn;
import src.PieceUtil.Piece;
import src.PieceUtil.Piece.ChessPiece;

/**
 * RuleUtils
 */
public class RuleUtils {

    private Game game;

    public RuleUtils(Game game) {
        this.game = game;
    }

    public boolean isCastlingMove() {
        if (game.currentColor == true) {
            game.activeP.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(game.prevCol, game.prevRow), game.getBitBoardWhite(),
                    game.squaresAttackedByOpposingTeam(game.currentColor));
        } else {
            game.activeP.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(game.prevCol, game.prevRow), game.getBitBoardBlack(),
                    game.squaresAttackedByOpposingTeam(game.currentColor));
        }
        if (Math.abs(game.activeP.col - game.prevCol) == 2 && simulateIfValidCastleMove()) {
            return true;
        }
        return false;
    }

     public boolean simulateIfValidCastleMove() {
        Piece simKing = game.getPieceObject(game.activeP.getPiece(), game.activeP.isWhite(), game.prevRow, game.prevCol);
        simKing.legalMoves = game.activeP.legalMoves;
        if (simKing.canMove(game.prevCol + 1, game.prevRow)) {
            long simBitBoard = game.currentColor == true ? game.getBitBoardWhite() : game.getBitBoardBlack();
            long newPos = game.bitBoardUtil.getBitBoardFromPos(game.prevCol + 1, game.prevRow);

            simBitBoard = ~game.bitBoardUtil.getBitBoardFromPos(game.prevCol, game.prevRow);
            simBitBoard &= newPos;
            if (game.activeP.isWhite()) {

                simKing.calculateLegalMoves(newPos, simBitBoard, game.squaresAttackedByOpposingTeam(simKing.isWhite()));
                if (simKing.canMove(game.prevCol + 2, game.prevRow)) {
                    return true;
                }
            }
            if (!game.activeP.isWhite()) {
                
                simKing.calculateLegalMoves(newPos, simBitBoard, game.squaresAttackedByOpposingTeam(simKing.isWhite()));
                if (simKing.canMove(game.prevCol + 2, game.prevRow)) {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean isEnPassant() {

        for (Piece piece : game.pieces) {
            if (game.activeP.col == piece.col && Math.abs(game.activeP.row - piece.row) == 1
                    && piece.getPiece() == ChessPiece.PAWN && piece.isWhite() != game.currentColor) {
                Pawn pawn = (Pawn) piece;
                if (pawn.hasDoubleMoved == true) {
                    return true;
                }
            }
        }
        return false;
    }

    public void captureWithEnPassant() {
        if (game.currentColor) {

            game.bitBoardUtil.removePieceFromBitBoard(game.bitBoardUtil.getBitBoardFromPos(game.activeP.col, game.activeP.row) >> 8, !game.currentColor);
        } else {
            game.bitBoardUtil.removePieceFromBitBoard(game.bitBoardUtil.getBitBoardFromPos(game.activeP.col, game.activeP.row) << 8, !game.currentColor);
        }
    }

    public void removeDoubleMovedForCurrentColor() {
        for (Piece piece : game.pieces) {
            if (piece.getPiece() == ChessPiece.PAWN && piece.isWhite() == game.currentColor) {
                Pawn pawn = (Pawn) piece;
                pawn.hasDoubleMoved = false;
            }
        }
    }

    public Piece getPinningPiece(long simBitBoard) {
        King king = game.getCurrentKing();
        Piece simPiece = null;
        for (Piece piece : game.pieces) {
            if (piece.isWhite() != game.activeP.isWhite() && piece.canMove(game.prevCol, game.prevRow)) {
                simPiece = game.getPieceObject(piece.getPiece(), piece.isWhite(), piece.row, piece.col);
                if (simPiece.isWhite()) {
                    simPiece.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(piece.col, piece.row), game.whitePieces,
                            simBitBoard);
                } else {
                    simPiece.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(piece.col, piece.row), simBitBoard,
                            game.blackPieces);
                }
                break;
            }
        }
        if (simPiece != null) {

            if (simPiece.canMove(king.col, king.row)) {
                return simPiece;
            }
        }
        return null;
    }

    public boolean isCheckingKing() {

        King king = game.getOpposingKing();
        if (game.activeP.getPiece() == ChessPiece.KING && game.currentColor == true) {

            game.activeP.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(game.activeP.col, game.activeP.row), game.getBitBoardWhite(),
                    game.squaresAttackedByOpposingTeam(game.currentColor));
        } else if (game.activeP.getPiece() == ChessPiece.KING && game.currentColor == false) {
            game.activeP.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(game.activeP.col, game.activeP.row), game.getBitBoardBlack(),
                    game.squaresAttackedByOpposingTeam(game.currentColor));
        } else {
            game.activeP.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(game.activeP.col, game.activeP.row), game.getBitBoardWhite(),
                    game.getBitBoardBlack());
        }
        if (game.activeP.canMove(king.col, king.row)) {
            return true;
        }
        return false;
    }

    public boolean isCheckMate() {
        if (canCaptureCheckingPiece()) {
            return false;
        }
        if (kingCanMove(game.getCurrentKing())) {
            return false;
        }
        if (pieceCanBlock(game.getCurrentKing())) {
            return false;
        }

        return true;
    }

    public boolean canCaptureCheckingPiece() {
        boolean canCaptureCheckingPiece = false;

        for (Piece piece : game.pieces) {
            if (piece.isWhite() == game.currentColor) {
                if (piece.getPiece() == ChessPiece.KING && piece.isWhite() == true) {
                    piece.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(piece.col, piece.row), game.getBitBoardWhite(),
                        game.squaresAttackedByOpposingTeam(game.currentColor));
                }
                else if(piece.getPiece() == ChessPiece.KING && piece.isWhite() == false){
                    piece.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(piece.col, piece.row), game.getBitBoardBlack(),
                        game.squaresAttackedByOpposingTeam(game.currentColor));
                }
                else{

                    piece.calculateLegalMoves(game.bitBoardUtil.getBitBoardFromPos(piece.col, piece.row), game.getBitBoardWhite(),
                            game.getBitBoardBlack());
                }
                if (game.checkingP != null && piece.canMove(game.checkingP.col, game.checkingP.row)) {
                    canCaptureCheckingPiece = true;
                    break;
                }

            }
        }

        return canCaptureCheckingPiece;
    }

    public boolean kingCanMove(King king) {
        if (game.simulateIfValidMove(king.col + 1, king.row, null) ||
                game.simulateIfValidMove(king.col + 1, king.row + 1, null) ||
                game.simulateIfValidMove(king.col + 1, king.row - 1, null) ||
                game.simulateIfValidMove(king.col - 1, king.row, null) ||
                game.simulateIfValidMove(king.col - 1, king.row + 1, null) ||
                game.simulateIfValidMove(king.col - 1, king.row + 1, null) ||
                game.simulateIfValidMove(king.col, king.row + 1, null) ||
                game.simulateIfValidMove(king.col, king.row - 1, null)) {
            return true;
        }
        return false;
    }

    public boolean pieceCanBlock(King king) {
        int colDiff = Math.abs(game.checkingP.col - king.col);
        int rowDiff = Math.abs(game.checkingP.row - king.row);

        if (colDiff == 0 && canBlockHorizontally(colDiff, rowDiff, king)) {
            return true;
        } else if (rowDiff == 0 && canBlockVertically(colDiff, rowDiff, king)) {
            return true;
        } else if (rowDiff == colDiff) {
            if (game.checkingP.row < king.row && canBlockLowerDiagonals(colDiff, rowDiff, king)) {
                return true;

            } else if (game.checkingP.row > king.row && canBlockUpperDiagonals(colDiff, rowDiff, king)) {
                return true;
            }

        }

        return false;

    }

    public boolean canBlockHorizontally(int colDiff, int rowDiff, King king) {
        if (game.checkingP.row > king.row) {
            for (int row = game.checkingP.col; row > king.col; row--) {

                for (Piece piece : game.pieces) {
                    if (piece != king && piece.isWhite() == game.currentColor && piece.canMove(game.checkingP.col, row)) {
                        return true;
                    }
                }
            }
        }
        if (game.checkingP.row < king.row) {
            for (int row = game.checkingP.col; row < king.col; row++) {

                for (Piece piece : game.pieces) {
                    if (piece != king && piece.isWhite() == game.currentColor && piece.canMove(game.checkingP.col, row)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canBlockVertically(int colDiff, int rowDiff, King king) {
        if (game.checkingP.col > king.col) {
            for (int col = game.checkingP.col; col > king.col; col--) {

                for (Piece piece : game.pieces) {
                    if (piece != king && piece.isWhite() == game.currentColor && piece.canMove(col, game.checkingP.row)) {
                        return true;
                    }
                }
            }
        }
        if (game.checkingP.col < king.col) {
            for (int col = game.checkingP.col; col < king.col; col++) {

                for (Piece piece : game.pieces) {
                    if (piece != king && piece.isWhite() == game.currentColor && piece.canMove(col, game.checkingP.row)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canBlockLowerDiagonals(int colDiff, int rowDiff, King king) {
        if (game.checkingP.col < king.col) {
            for (int row = game.checkingP.row, col = game.checkingP.col; col < king.col; col++, row++) {
                for (Piece piece : game.pieces) {
                    if (piece != king && piece.isWhite() == game.currentColor && piece.canMove(col, row)) {
                        return true;
                    }
                }
            }
        } else if (game.checkingP.col > king.col) {
            for (int row = game.checkingP.row, col = game.checkingP.col; col > king.col; col--, row++) {
                for (Piece piece : game.pieces) {
                    if (piece != king && piece.isWhite() == game.currentColor && piece.canMove(col, row)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canBlockUpperDiagonals(int colDiff, int rowDiff, King king) {
        if (game.checkingP.col < king.col) {
            for (int row = game.checkingP.row, col = game.checkingP.col; col < king.col; col++, row--) {
                for (Piece piece : game.pieces) {
                    if (piece != king && piece.isWhite() == game.currentColor && piece.canMove(col, row)) {
                        return true;
                    }
                }
            }
        } else if (game.checkingP.col > king.col) {
            for (int row = game.checkingP.row, col = game.checkingP.col; col > king.col; col--, row--) {
                for (Piece piece : game.pieces) {
                    if (piece != king && piece.isWhite() == game.currentColor && piece.canMove(col, row)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}