package tetris.game;

public enum TetronimoType {
    I_Tetronimo(new int[][] {{-2,0}, {-1,0}, {0,0}, {1,0}}),
    J_Tetronimo(new int[][] {{-2,0}, {-1,0}, {0,0}, {0,1}}),
    L_Tetronimo(new int[][] {{-2,1}, {-2,0}, {-1,0}, {0,0}}),
    O_Tetronimo(new int[][] {{-1,1}, {-1,0}, {0,0}, {0,1}}),
    Z_Tetronimo(new int[][] {{-2,0}, {-1,0}, {-1,1}, {0,1}}),
    S_Tetronimo(new int[][] {{-2,1}, {-1,1}, {-1,0}, {0,0}}),
    T_Tetronimo(new int[][] {{-2,1}, {-1,1}, {-1,0}, {0,1}});

    TetronimoType(int[][] tetronimo) {
        this.tetronimo = tetronimo;
//        pos = new int[4][2];
//        reset();
    }

//    public void reset() {
//        for (int i = 0; i < pos.length; i++) {
//            pos[i] = tetronimo[i].clone();
//        }
//    }
//
//    public void rotate() {
//        if (this != Tetronimo.O_Tetronimo) {
//            // Clockwise rotation
//            for (int[] coords : pos) {
//                int tmp = coords[0];
//                coords[0] = -coords[1];
//                coords[1] = tmp;
//            }
//        }
//    }

    final int[][] tetronimo;
//            , pos;
}