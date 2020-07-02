package tetris.game;

public class Tetronimo {
    final int[][] pos;
    TetronimoType tetronimoType;

    public Tetronimo(TetronimoType tetronimoType) {
        pos = new int[4][2];
        this.tetronimoType = tetronimoType;
        reset();
    }

    public void reset() {
        for (int i = 0; i < pos.length; i++) {
            pos[i] = tetronimoType.tetronimo[i].clone();
        }
    }

    public void rotate() {
        if (tetronimoType != TetronimoType.O_Tetronimo) {
            // Clockwise rotation
            for (int[] coords : pos) {
                int tmp = coords[0];
                coords[0] = -coords[1];
                coords[1] = tmp;
            }
        }
    }
}

