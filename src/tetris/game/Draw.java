package tetris.game;

import java.awt.*;

import static tetris.game.Config.rectSize;

public class Draw {
    public static void drawRect(Graphics2D g, double x, int y, Color color, Color borderColor) {
        g.setColor(color);

        g.fillRect((int) (x * rectSize), y * rectSize, rectSize, rectSize);
        g.setStroke(new BasicStroke(1));
        g.setColor(borderColor);
        g.drawRect((int) (x * rectSize), y * rectSize, rectSize, rectSize);
    }
}
