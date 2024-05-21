package src.ChessGame;
import javax.swing.*;
import java.awt.*;

public class ChessBoard extends JFrame {
    private final int AMOUNT_OF_COL = 8;
    private final int AMOUNT_OF_ROW = 8;
    public static final int SQUARE_SIZE = 100;

    public void draw(Graphics2D g2) {

        int iteration = 0;

        for (int row = 0; row < AMOUNT_OF_ROW; row++) {
            for (int col = 0; col < AMOUNT_OF_COL; col++) {
                if (iteration % 2 == 0) {
                    g2.setColor(new Color(210, 165, 125));
                }
                else{
                    g2.setColor(new Color(175, 115, 70));
                }
                g2.fillRect(col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                iteration++;
            }
            iteration++;
        }
    }
}