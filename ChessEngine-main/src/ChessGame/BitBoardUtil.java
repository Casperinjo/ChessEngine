
package src.ChessGame;

import java.util.ArrayList;
import java.util.Iterator;

import src.PieceUtil.King;
import src.PieceUtil.Piece;
import src.PieceUtil.Piece.ChessPiece;
import src.PieceUtil.Rook;

public class BitBoardUtil {
    private Game game;
    public BitBoardUtil(Game game) {
        this.game = game;
    }
    public boolean isPieceOnSquare(long bitBoard, int square) {

        long mask = 1L << square;

        return (bitBoard & mask) != 0L;

    }

    public boolean isLegal(long legalMoves, int col, int row) {
        long pos = getBitBoardFromPos(col, row);
        return ((legalMoves & pos) != 0L);
    }



    public long getBitBoardFromPos(int col, int row) {
        int index = row * 8 + col;
        long pos = 1L <<  index; // 63 - index to make 'h8' the MSB

        return pos;
    }

    public void changeBitBoardAfterMove(ChessPiece type, boolean isWhite, int fromSquare, int toSquare) {
        long fromBit = 1L << fromSquare;
        long toBit = 1L << toSquare;

        // Check if the destination square is occupied by an opponent's piece
        if ((toBit & (isWhite ? game.getBitBoardBlack() : game.getBitBoardWhite())) != 0) {
            removePieceFromBitBoard(toBit, !isWhite);
        }

        // Update the bitboard for the piece that moved
        updatePieceBitboard(type, isWhite, toBit, fromBit);
        updateAggregateBitboards();

    }

    public void removePieceFromBitBoard(long bit, boolean isWhite) {

        Iterator<Piece> it = game.pieces.iterator();
        while (it.hasNext()) {
            Piece piece = it.next();
            long positionBit = 1L << (piece.row * 8 + piece.col);
            // Check if the bit matches and if the piece color is not the color of the piece
            // that is moving
            if ((positionBit & bit) != 0 && piece.isWhite() == isWhite) {
                it.remove(); // Remove the piece from the graphical list
                break;
            }
        }
        // Remove from the correct bitboard based on the captured piece's color, not the
        // moving piece's color
        if (isWhite) { // if the moving piece is black, remove a white piece
            if ((bit & game.wPawns) != 0)
                game.wPawns &= ~bit;
            else if ((bit & game.wKnights) != 0)
                game.wKnights &= ~bit;
            else if ((bit & game.wBishops) != 0)
                game.wBishops &= ~bit;
            else if ((bit & game.wRooks) != 0)
                game.wRooks &= ~bit;
            else if ((bit & game.wQueens) != 0)
                game.wQueens &= ~bit;
            else if ((bit &game. wKing) != 0)
                game.wKing &= ~bit;
        } else { // if the moving piece is white, remove a black piece
            if ((bit & game.bPawns) != 0)
                game.bPawns &= ~bit;
            else if ((bit & game.bKnights) != 0)
                game.bKnights &= ~bit;
            else if ((bit & game.bBishops) != 0)
                game.bBishops &= ~bit;
            else if ((bit & game.bRooks) != 0)
                game.bRooks &= ~bit;
            else if ((bit & game.bQueens) != 0)
                game.bQueens &= ~bit;
            else if ((bit & game.bKing) != 0)
                game.bKing &= ~bit;
        }
    }

    public void updateAggregateBitboards() {
        game.whitePieces = game.wPawns | game.wKnights | game.wBishops |game. wRooks | game.wQueens | game.wKing;
        game.blackPieces = game.bPawns | game.bKnights | game.bBishops | game.bRooks | game.bQueens | game.bKing;
        game.allPieces = game.whitePieces | game.blackPieces;
    }

    public long movePiece(long bitBoard, long fromBit, long toBit) {
        return (bitBoard & ~fromBit) | toBit;
    }

    public void updatePieceBitboard(ChessPiece type, boolean isWhite, long toBit, long fromBit) {
        if (isWhite) {
            switch (type) {
                case PAWN:
                    game.wPawns = movePiece(game.wPawns, fromBit, toBit);
                    break;
                case KNIGHT:
                    game.wKnights = movePiece(game.wKnights, fromBit, toBit);
                    break;
                case BISHOP:
                    game.wBishops = movePiece(game.wBishops, fromBit, toBit);
                    break;
                case ROOK:
                    game.wRooks = movePiece(game.wRooks, fromBit, toBit);
                    break;
                case QUEEN:
                    game.wQueens = movePiece(game.wQueens, fromBit, toBit);
                    break;
                case KING:
                    game.wKing = movePiece(game.wKing, fromBit, toBit);
                    break;
            }
        } else {
            switch (type) {
                case PAWN:
                    game.bPawns = movePiece(game.bPawns, fromBit, toBit);
                    break;
                case KNIGHT:
                    game.bKnights = movePiece(game.bKnights, fromBit, toBit);
                    break;
                case BISHOP:
                    game.bBishops = movePiece(game.bBishops, fromBit, toBit);
                    break;
                case ROOK:
                    game.bRooks = movePiece(game.bRooks, fromBit, toBit);
                    break;
                case QUEEN:
                    game.bQueens = movePiece(game.bQueens, fromBit, toBit);
                    break;
                case KING:
                    game.bKing = movePiece(game.bKing, fromBit, toBit);
                    break;
            }
        }
    }

    public void updateBitboardsAfterCastling(King king, Rook rook, int oldRookCol, int oldRookRow, boolean kingside) {
        long kingFromBit = 1L << (game.prevRow * 8 + game.prevCol);
        long kingToBit = 1L << (king.row * 8 + king.col);
        long rookFromBit = 1L << (oldRookRow * 8 + oldRookCol);
        long rookToBit = 1L << (rook.row * 8 + rook.col);

        long kingAndRookClearMask = kingFromBit | rookFromBit;
        if (king.isWhite()) {
            game.whitePieces &= ~kingAndRookClearMask; // Clear old king and rook positions in whitePieces
        } else {
            game.blackPieces &= ~kingAndRookClearMask; // Clear old king and rook positions in blackPieces
        }

        long kingAndRookSetMask = kingToBit | rookToBit;
        if (king.isWhite()) {
            game.whitePieces |= kingAndRookSetMask; // Set new king and rook positions in whitePieces
        } else {
            game.blackPieces |= kingAndRookSetMask; // Set new king and rook positions in blackPieces
        }

        game.allPieces = game.whitePieces | game.blackPieces;

        if (king.isWhite()) {
            game.wKing = (game.wKing & ~kingFromBit) | kingToBit;
            game.wRooks = (game.wRooks & ~rookFromBit) | rookToBit;
        } else {
            game.bKing = (game.bKing & ~kingFromBit) | kingToBit;
            game.bRooks = (game.bRooks & ~rookFromBit) | rookToBit;
        }
    }

}