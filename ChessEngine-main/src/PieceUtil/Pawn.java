package src.PieceUtil;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.ChessGame.Game;

public class Pawn extends Piece {
   
    private static final long RANK_2 = 0x000000000000FF00L; // Home rank for white pawns.
    private static final long RANK_7 = 0x00FF000000000000L; // Home rank for black pawns.
    private static final long RANK_1 = 0x00000000000000FFL; // Promotion rank for black.
    private static final long RANK_8 = 0xFF00000000000000L; // Promotion rank for white.

    public boolean hasDoubleMoved;

    public Pawn(ChessPiece piece, boolean isWhite, int row, int col, Game game) {
        super(piece, isWhite, row, col, game);
    }

    public void setImage() {

        try {

            if (isWhite) {
                File file = new File(
                        "./Images/piece_0_5.png");
                this.image = ImageIO.read(file);
            } else {
                File file = new File(
                        "./Images/piece_1_5.png");
                this.image = ImageIO.read(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that generates legal moves for the pawn
     * 
     * @param position  The bitboard position for the pawn
     * @param occupancy The bitboard for all the pieces on the board
     * @return bitboard that contain all the legal moves for pawn depending on the
     *         color
     */
    public void calculateLegalMoves(long position, long whitePieces, long blackPieces) {
        // Initialize a bitboard for legal moves
        if (isWhite()) {
            legalMoves = generateLegalMovesForWhite(position, whitePieces, blackPieces);
           
        } else {

            legalMoves = generateLegalMovesForBlack(position, whitePieces, blackPieces);
        }

    }

    /**
     * Generates moves for white
     * 
     * @param position  The bitboard position for the pawn
     * @param occupancy The bitboard for all the pieces on the board
     * @return
     */
    public long generateLegalMovesForWhite(long position, long whitePieces, long blackPieces) {

        long moves = 0L;
        long occupancy = whitePieces | blackPieces;
        long emptySquares = ~occupancy;

        // Calculate single square moves forward
        long singleMove = (position << 8) & ~blackPieces;

        // Calculate double moves
        // Check if the pawn is on the second rank and if the square two steps ahead is
        // also empty
        
        if ((position & RANK_2) != 0L && singleMove != 0L) {

            long doubleMove = position << 16 & emptySquares;
            moves |= doubleMove;
        } // Second rank

        // Captures to the left and right
        long notAFile = 0xFEFEFEFEFEFEFEFEL; // Correctly masks off the 'a' file
        long notHFile = 0x7F7F7F7F7F7F7F7FL; // Correctly masks off the 'h' file
        long capturesLeft = (position & notAFile) << 7 & blackPieces;
        attackingMoves |= (position & notAFile) << 7;
        // Apply correct mask for 'a' file
        long capturesRight = (position & notHFile) << 9 & blackPieces;
        attackingMoves |= (position & notHFile) << 9; // Apply correct mask for 'h' file

        if ((singleMove & RANK_8) != 0) { // Check if moving into the promotion rank
            // promotion(this, position << 8, true);
        } else {
            moves |= singleMove;
        }

        // Captures with promotion check
        if ((capturesLeft & RANK_8) != 0) {
            // promotion(this, position << 7, true);
        } else {
            moves |= capturesLeft;
        }

        if ((capturesRight & RANK_8) != 0) {
            // promotion(this, position << 9, true);
        } else {
            moves |= capturesRight;
        }

        long enPassantLeft = (position << 7) & ~FILE_H;

        return moves;
    }

    /**
     * Generates moves for black
     * 
     * @param position  The bitboard position for the pawn
     * @param occupancy The bitboard for all the pieces on the board
     * @return
     */
    public long generateLegalMovesForBlack(long position, long whitePieces, long blackPieces) {
        long moves = 0L;
        long occupancy = whitePieces & blackPieces;
        long emptySquares = ~(occupancy);

        // Calculate single square moves forward
        long singleMove = (position >> 8) & ~whitePieces;

        // Calculate double moves
        // Check if the pawn is on the second rank and if the square two steps ahead is
        // also empty
        if ((position & RANK_7) != 0L && singleMove != 0L) {

            long doubleMove = position >> 16 & emptySquares;
            moves |= doubleMove;
        } // Second rank

        // Captures to the left and right
        long notAFile = ~0xFEFEFEFEFEFEFEFEL; 
        long notHFile = ~0x7F7F7F7F7F7F7F7FL;
         
        long capturesLeft = (position & notAFile) >> 7 & whitePieces; // Apply correct mask for 'a' file
        attackingMoves |= (position & notAFile) >> 7; // Apply correct mask for 'a' file
        long capturesRight = (position & notHFile) >> 9 & whitePieces;
        attackingMoves |= (position & notHFile) >> 9;// Apply correct mask for 'h' file

        if ((singleMove & RANK_1) != 0) { // Check if moving into the promotion rank
            // promotion(this, position >> 8, true);
        } else {
            moves |= singleMove;
        }

        // Captures with promotion check
        if ((capturesLeft & RANK_8) != 0) {
            // promotion(this, position >> 7, true);
        } else {
            moves |= capturesLeft;
        }

        if ((capturesRight & RANK_8) != 0) {
            // promotion(this, position >> 9, true);
        } else {
            moves |= capturesRight;
        }

        long enPassantLeft = (position >> 7) & ~FILE_H;

        return moves;
    }

    public void promotion(Pawn pawn, long position, boolean isWhite, ChessPiece type) {

        System.out.println("Pawn promoted at position " + position + " to ");
    }

}
