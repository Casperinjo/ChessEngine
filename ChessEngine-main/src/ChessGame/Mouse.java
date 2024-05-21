package src.ChessGame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
    private int x, y;

    public boolean pressed;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void mousePressed(MouseEvent e) {
        pressed = true;
        
    }

    public void mouseReleased(MouseEvent e) {
        pressed = false;
    }

    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }
}