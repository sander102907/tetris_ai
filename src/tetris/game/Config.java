package tetris.game;

import java.awt.*;

public final class Config {
    final static int height = 1000;
    final static int rows = 20;
    final static int columns = 10;
    final static int scoreBoardWidth = 300;
    final static int rectSize = height/rows;
    final static Color[] tetronimoColors = {
            new Color(85, 159, 219), // I piece
            new Color(16, 8, 224), //J piece
            new Color(255, 151, 28), // L piece
            new Color(255, 213, 0), // O piece
            new Color(255, 50, 19), // Z piece
            new Color(114, 203, 59), // S piece
            new Color(193, 40, 170) // T piece
    };

    final static Color[] tetronimoBorderColors = {
            new Color(45, 119, 179), // I piece
            new Color(16, 8, 184), //J piece
            new Color(215, 131, 28), // L piece
            new Color(215, 173, 0), // O piece
            new Color(215, 10, 19), // Z piece
            new Color(74, 163, 49), // S piece
            new Color(143, 10, 130) // T piece
    };

    final static String soundPath = "./resources/sounds/";

    final static int scoreBoardMargin = 30;

    final static int lineScore(int level, int amtLines) {
        if (amtLines == 1) {
            return 40 * (level + 1);
        } else if (amtLines == 2) {
            return 100 * (level + 1);
        } else if (amtLines == 3) {
            return 300 * (level + 1);
        } else if (amtLines == 4) {
            return 1200 * (level + 1);
        }

        return 0;
    }

    final static int softDropSpeed = 40;

    // https://tetris.wiki/Tetris_(NES,_Nintendo)
    final static double[] levelSpeeds = {
            (double) 48/60 * 1000, // level 0
            (double) 43/60 * 1000, // level 1
            (double) 38/60 * 1000, // level 2,
            (double) 33/60 * 1000, // level 3,
            (double) 28/60 * 1000, // level 4,
            (double) 23/60 * 1000, // level 5,
            (double) 18/60 * 1000, // level 6,
            (double) 13/60 * 1000, // level 7,
            (double) 8/60 * 1000, // level 8,
            (double) 6/60 * 1000, // level 9,
            (double) 5/60 * 1000, // level 10,
            (double) 5/60 * 1000, // level 11,
            (double) 5/60 * 1000, // level 12,
            (double) 4/60 * 1000, // level 13,
            (double) 4/60 * 1000, // level 14,
            (double) 4/60 * 1000, // level 15,
            (double) 3/60 * 1000, // level 16,
            (double) 3/60 * 1000, // level 17,
            (double) 3/60 * 1000, // level 18,
            (double) 2/60 * 1000, // level 19,
            (double) 2/60 * 1000, // level 20,
            (double) 2/60 * 1000, // level 21,
            (double) 2/60 * 1000, // level 22,
            (double) 2/60 * 1000, // level 23,
            (double) 2/60 * 1000, // level 24,
            (double) 2/60 * 1000, // level 25,
            (double) 2/60 * 1000, // level 26,
            (double) 2/60 * 1000, // level 27,
            (double) 2/60 * 1000, // level 28,
            (double) 1/60 * 1000, // level 29+,
    };

}
