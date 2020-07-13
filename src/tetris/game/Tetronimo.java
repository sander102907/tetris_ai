package tetris.game;

import java.io.Serializable;

public class Tetronimo implements Serializable {
    private static final long serialVersionUID = 1L;
    final int[][] pos;
    private int rotation = 0;
    TetronimoType tetronimoType;

    public Tetronimo(TetronimoType tetronimoType) {
        pos = new int[4][2];
        this.tetronimoType = tetronimoType;
        reset();
    }

    public void reset() {
        rotation = 0;
        for (int i = 0; i < pos.length; i++) {
            pos[i] = tetronimoType.tetronimo[rotation][i].clone();
        }
    }

    public void rotate() {
        rotation = (rotation + 1) % 4;
        for (int i = 0; i < pos.length; i++) {
            pos[i] = tetronimoType.tetronimo[rotation][i].clone();
        }
    }

    public int[][] getRotationPos() {
        int[][] rotationPos = new int[4][2];
        for (int i = 0; i < pos.length; i++) {
            rotationPos[i] = tetronimoType.tetronimo[(rotation + 1) % 4][i].clone();
        }
        return rotationPos;
    }

    public TetronimoType getTetronimoType() {
        return tetronimoType;
    }
}

