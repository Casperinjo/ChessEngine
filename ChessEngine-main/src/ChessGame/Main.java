package src.ChessGame;

import javax.swing.JFrame;

/**
 * Main
 */
public class Main {

    public static void main(String[] args) {

        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        Game game = new Game();
        window.add(game);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        game.startGame();

    }
}