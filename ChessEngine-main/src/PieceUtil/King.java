package src.PieceUtil;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.ChessGame.BitBoardUtil;
import src.ChessGame.ChessBoard;
import src.ChessGame.Game;

public class King extends Piece {

    private boolean isChecked;
    private boolean hasNotMoved;
    private BitBoardUtil bitBoardUtil;
    

    public King(ChessPiece piece, boolean isWhite, int row, int col, Game game) {
        super(piece, isWhite, row, col, game);
        bitBoardUtil = new BitBoardUtil(game);
    }

    public void setImage() {

        try {

            if (isWhite) {
                File file = new File("./Images/piece_0_0.png");
                this.image = ImageIO.read(file);
            } else {
                File file = new File("./Images/piece_1_0.png");
                this.image = ImageIO.read(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates all the legal moves for the king
     * 
     * @param posistion    Position of the king (bitboard)
     * @param occupancy    Position of all enemypieces (bitboard)
     * @param enemyAttacks Squares that are attacked by the enemy pieces (bitboard)
     * @return
     */
    public void calculateLegalMoves(long posistion, long occupancy, long enemyAttacks) {
        // Stores all the legal moves for the king
        legalMoves = calculateKingRegularMoves(posistion, occupancy, enemyAttacks);

        // Checks if king is eligible for castling
        /*
         * if (hasNotMoved && !isChecked) {
         * legalMoves |= calculateCastlingMoves(posistion, occupancy,
         * move.squaresAttackedByCurrentPlayer(move.getPlayer()), this.isWhite());
         * }
         */

    }

    public long calculateKingRegularMoves(long position, long occupancy, long enemyAttacks) {
        long moves = 0L;
        long emptySqaures = ~occupancy;
        long safePositions = ~enemyAttacks;

        // Move North
        long moveNorth = (position << 8) & emptySqaures & safePositions;
        long guardNorth = (position << 8) & occupancy & safePositions;

        // Move South
        long moveSouth = (position >> 8) & emptySqaures & safePositions;
        long guardSouth = (position >> 8) & occupancy & safePositions;
        // Move East
        long moveEast = (position << 1) & emptySqaures & safePositions & ~FILE_A;
        long guardEast = (position << 1) & occupancy & safePositions & ~FILE_A;

        // Move West
        long moveWest = (position >> 1) & emptySqaures & safePositions & ~FILE_H;
        long guardWest = (position >> 1) & occupancy & safePositions & ~FILE_H;

        // Move Northeast
        long moveNorthEast = (position << 9) & emptySqaures & safePositions & ~FILE_A;
        long guardNorthEast = (position << 9) & occupancy & safePositions & ~FILE_A;

        // Move Northwest
        long moveNorthWest = (position << 7) & emptySqaures & safePositions & ~FILE_H;
        long guardNorthWest = (position << 7) & occupancy & safePositions & ~FILE_H;

        // Move Southeast
        long moveSouthEast = (position >> 9) & emptySqaures & safePositions & ~FILE_A;
        long guardSouthEast = (position >> 9) & occupancy & safePositions & ~FILE_A;

        // Move Southwest
        long moveSouthWest = (position >> 7) & emptySqaures & safePositions & ~FILE_H;
        long guardSouthWest = (position >> 7) & occupancy & safePositions & ~FILE_H;

        moves = moveSouth | moveEast | moveWest | moveNorth | moveNorthEast | moveNorthWest | moveSouthEast
                | moveSouthWest;
        attackingMoves = legalMoves | guardSouth | guardEast | guardWest | guardNorth | guardNorthEast | guardNorthWest
                | guardSouthEast | guardSouthWest;
       
        return moves;

    }

    public void castle(boolean kingside, Game game) {
    int kingTargetColumn = kingside ? 6 : 2; // Correct target columns for castling
    int rookStartColumn = kingside ? 7 : 0;
    int rookEndColumn = kingside ? 5 : 3;


    Rook rook = (Rook) game.findPieceAt(rookStartColumn, row);
    if (rook != null) {
        // Update the King's position
        int oldKingCol = col;
        col = kingTargetColumn; // Correctly update column for king
        x = col * ChessBoard.SQUARE_SIZE; // Update graphical coordinate
        y = (7 - row) * ChessBoard.SQUARE_SIZE; // Assuming the board is drawn from the top

        // Update the Rook's position
        int oldRookCol = rook.col;
        int oldRookRow = rook.row;
        rook.col = rookEndColumn; // Correctly update column for rook
        rook.x = rook.col * ChessBoard.SQUARE_SIZE; // Update graphical coordinate
        rook.y = (7 - row) * ChessBoard.SQUARE_SIZE; // Assuming the board is drawn from the top

        System.out.println("King final position: " + row + "," + col);
        System.out.println("Rook final position: " + row + "," + rook.col);

        // Reflect these changes on the bitboards
        bitBoardUtil.updateBitboardsAfterCastling(this, rook, oldRookCol, oldRookRow, kingside);

        // Log board state after castling
    } else {
        System.out.println("Error: Rook not found for castling at " + rookStartColumn);
    }
}


}
