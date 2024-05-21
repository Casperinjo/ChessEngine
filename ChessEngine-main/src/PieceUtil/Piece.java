package src.PieceUtil;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import src.ChessGame.BitBoardUtil;
import src.ChessGame.ChessBoard;
import src.ChessGame.Game;

abstract public class Piece {

    public enum ChessPiece {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    protected BufferedImage spriteSheet;

    protected BufferedImage image;
    protected final int PIECE_WIDTH = 166;
    protected final int PIECE_HEIGHT = 334;

    protected ChessPiece piece;
    protected boolean isWhite;
    private boolean isChecked;

    public int x, y;
    public int row, col; // Current position
    public int prevRow, prevCol;

    public long legalMoves;
    public long attackingMoves;

    protected  final long FILE_A = 0x0101010101010101L;
    protected  final long FILE_B = 0x202020202020202L;
    protected  final long FILE_H = 0x8080808080808080L;
    protected  final long FILE_G = 0x4040404040404040L;
    
    
    private BitBoardUtil bitBoardUtil;

    public Piece(ChessPiece piece, boolean isWhite, int row, int col, Game game) {
        this.piece = piece;
        this.isWhite = isWhite;
        this.row = row;
        this.col = col;

        this.bitBoardUtil = new BitBoardUtil(game);

        setPos(col * ChessBoard.SQUARE_SIZE, (7 - row) * ChessBoard.SQUARE_SIZE);

        setImage();
    }

    public void move(int newRow, int newCol) {
        // Before changing the position, update the previous position
        this.prevRow = this.row;
        this.prevCol = this.col;

        // Change to the new position
        this.row = newRow;
        this.col = newCol;
    }

    public int getCol(int pixelX) {
        return (x + ChessBoard.SQUARE_SIZE / 2) / ChessBoard.SQUARE_SIZE;
    }

    public int getRow(int pixelY) {
        return 7 - (y + ChessBoard.SQUARE_SIZE / 2) / ChessBoard.SQUARE_SIZE;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    abstract void setImage();

    public BufferedImage getImage() {
        return image;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public int getPrevRow() {
        return prevRow;
    }

    public int getPrevCol() {
        return prevCol;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void drawPiece(Graphics2D g2) {

        g2.drawImage(image, x, y, ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE,
                null);

    }

    public boolean canMove(int col, int row) {
        long moveTo = bitBoardUtil.getBitBoardFromPos(col, row);
        
        return (legalMoves & moveTo) != 0L;
    }

    public abstract void calculateLegalMoves(long bitboard, long whitePieces , long blackPieces);

}