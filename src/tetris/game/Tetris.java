package tetris.game;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.Timer;

import static tetris.game.Config.*;
import static tetris.game.Draw.drawRect;

public class Tetris extends JPanel implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;
    static final Random rand = new Random();
    private int currentRow;
    private int currentColumn;
    private int[][] grid = new int[columns][rows];
    private Tetronimo currentTetronimo;
    private Tetronimo nextTetronimo;
    private Boolean softDrop = false;
    private Boolean hardDrop = false;
    private Boolean paused = false;
    private Boolean gameOver = false;
    private Boolean pieceLanded = false;

    private Set<Integer>  randomTetronimos;

    private transient Timer timer;
    private Thread gravityThread;
    private ScoreBoard scoreBoard;

    public Tetris() {
        setPreferredSize(new Dimension(height/2, height));
        setBorder(BorderFactory.createLineBorder(Color.white));
        setBackground(Color.black);
    }

    private void getTetronimo() {
        currentTetronimo = nextTetronimo;
        nextTetronimo = getRandomTetronimo();
        scoreBoard.drawPreviewTetronimo(nextTetronimo);
        currentRow = 2;
        currentColumn = 4;
    }

    public void moveLeft() {
        if (canMoveLeft(grid, currentRow, currentColumn, currentTetronimo)) {
            currentColumn--;
            SoundPlayer.play("move.wav", false);
        }
        repaint();
    }

    public void moveRight() {
        if (canMoveRight(grid, currentRow, currentColumn, currentTetronimo)) {
            currentColumn++;
            SoundPlayer.play("move.wav", false);
        }
        repaint();
    }

    public void rotateTetronimo() {
        if (canRotate(grid, currentRow, currentColumn, currentTetronimo)) {
            currentTetronimo.rotate();
            SoundPlayer.play("rotate.wav", false);
        }
        repaint();
    }

    public void moveDown() {
        if (!softDrop) {
            softDrop = true;
            gravityThread.interrupt();
        }
        repaint();
    }

    public void hardDrop() {
        hardDrop = true;
        while(canMoveDown(grid, currentRow, currentColumn, currentTetronimo)) {
            currentRow += 1;
            scoreBoard.addScore(2);
        }
        gravityThread.interrupt();
        repaint();
    }

    public void setSoftDrop(Boolean softDrop) {
        this.softDrop = softDrop;
        repaint();
    }

    public void setPause() {
        if (!paused) {
            SoundPlayer.play("pause.wav", false);
            paused = true;
            stop();
        } else {
            paused = false;
            gravityThread = new Thread(this);
            gravityThread.start();
            timer = new Timer(true);
            timer.schedule(new GameTimer(scoreBoard), 0, 1000);
        }
    }

    public Boolean getPause() {
        return paused;
    }

    // Random Generator generates a sequence of all seven one-sided tetrominoes (I, J, L, O, S, T, Z) permuted
    // randomly, as if they were drawn from a bag. Then it deals all seven tetrominoes to the piece sequence
    // before generating another bag.
    private Tetronimo getRandomTetronimo() {
        if (randomTetronimos == null || randomTetronimos.isEmpty()) {
            randomTetronimos = new LinkedHashSet<>();
            while (randomTetronimos.size() < TetronimoType.values().length) {
                randomTetronimos.add(rand.nextInt(TetronimoType.values().length));
            }
        }

        Iterator<Integer> it = randomTetronimos.iterator();
        int randomTetronimo = it.next();
        it.remove();


        return new Tetronimo(TetronimoType.values()[randomTetronimo]);
    }

    public void start() {
        initGrid();

        timer = new Timer(true);
        timer.schedule(new GameTimer(scoreBoard), 0, 1000);


        nextTetronimo = getRandomTetronimo();
        softDrop = false;
        getTetronimo();

        gravityThread = new Thread(this);
        gravityThread.start();
    }


    public void stop() {
        if (gravityThread != null) {
            Thread temp = gravityThread;
            gravityThread = null;
            temp.interrupt();
            timer.cancel();
            timer.purge();

            if (!paused) {
                GameFilesOperator.setScore(scoreBoard.getScore());
            }
        }
    }

    @Override
    public void run() {
        while (Thread.currentThread() == gravityThread) {
            try {
                if (paused) {
                    synchronized (gravityThread) {
                        try {
                            gravityThread.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }

                if (currentRow < 1 && canMoveDown(grid, currentRow, currentColumn, currentTetronimo)) {
                    currentRow++;
                }

                if (softDrop) {
                    Thread.sleep(softDropSpeed);
                    scoreBoard.addScore(1);
                    SoundPlayer.play("softdrop.wav", false);
                } else {
                    Thread.sleep((int) levelSpeeds[scoreBoard.getLevel()]);
                }

            } catch (InterruptedException e) {
                if (hardDrop) {
                    hardDrop = false;
                    tetronimoLanded();
                    SoundPlayer.play("lock.wav", false);
                }
                repaint();
                continue;
            }

            if (canMoveDown(grid, currentRow, currentColumn, currentTetronimo)) {
                currentRow += 1;
            } else {
                if (!hardDrop) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        continue;
                    }
                } else {
                    hardDrop = false;
                }

                if (canMoveDown(grid, currentRow, currentColumn, currentTetronimo)) {
                    currentRow += 1;
                } else {
                    tetronimoLanded();
                    SoundPlayer.play("lock.wav", false);
                }
            }

            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawGridLines(g);
        drawGridTetronimos(g);
        drawTetronimoDropPreview(g);
        if (currentTetronimo != null) {
            drawTetronimo(g, currentTetronimo);
        }
    }

    private void drawTetronimo(Graphics2D g, Tetronimo tetronimo) {
        if (currentRow > 1) {
            Color color = tetronimoColors[tetronimo.tetronimoType.ordinal()];
            Color borderColor = tetronimoBorderColors[tetronimo.tetronimoType.ordinal()];

            for (int[] coords : tetronimo.pos) {
                drawRect(g, coords[0] + currentColumn, coords[1] + currentRow - 2, color, borderColor);
            }
        }
    }

    private void initGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = -1;
            }
        }
    }

    public Boolean canMoveDown(int[][] grid, int currentRow, int currentColumn, Tetronimo tetronimo) {
        for (int[] coords: tetronimo.pos) {
            if (
//                    currentRow + coords[1] + 1 >= 0 &&
//                    currentColumn + coords[0] < grid.length &&
//                    currentColumn + coords[0] >= 0 &&
                   (currentRow + coords[1] >= grid[0].length - 1 ||
                    grid[currentColumn + coords[0]][currentRow + coords[1] + 1] != -1)
            ) {
                return false;
            }
        }

        return true;
    }

    public Boolean canMoveLeft(int[][] grid, int currentRow, int currentColumn, Tetronimo tetronimo) {

        for (int[] coords: tetronimo.pos) {
            if (
//                    currentRow + coords[1] >= 0 &&
                    (currentColumn + coords[0] <= 0 ||
                    grid[currentColumn + coords[0] -1][currentRow + coords[1]] != -1)
            ) {
                return false;
            }
        }
        return true;
    }

    public Boolean canRotate(int[][] grid, int currentRow, int currentColumn, Tetronimo tetronimo) {
        int[] offsets = getRotationOffsets(grid, currentColumn, tetronimo);


        for (int[] coords: tetronimo.getRotationPos()) {
            if (
                    currentRow + coords[1] < 0 ||
                    currentRow + coords[1] >= grid[0].length ||
//                    currentColumn + coords[1] < 0 ||
//                    currentColumn + coords[1] >= grid.length ||
                    grid[coords[0] + currentColumn - offsets[0] - offsets[1]][coords[1] + currentRow] != -1
            ) {
                return false;
            }
        }

        currentColumn -= offsets[0] + offsets[1];
        return true;
    }

    public Boolean canMoveRight(int[][] grid, int currentRow, int currentColumn, Tetronimo tetronimo) {
        for (int[] coords: tetronimo.pos) {
            if (
//                    currentRow + coords[1] >= 0 &&
                    (currentColumn + coords[0] >= grid.length - 1 ||
                    grid[currentColumn + coords[0] + 1][currentRow + coords[1]] != -1)
            ) {
                return false;
            }
        }
        return true;
    }

    public int[] getRotationOffsets(int[][] grid, int currentColumn, Tetronimo tetronimo) {
        int rightOffset = 0;
        int leftOffset = 0;

        for (int[] coords: tetronimo.getRotationPos()) {
            // If after rotations, will be out of grid on left side
            if (coords[0] + currentColumn < leftOffset) {
                leftOffset = coords[0] + currentColumn;
                // If after rotations, will be out of grid on right side
            } else if (coords[0] + currentColumn - grid.length + 1 > rightOffset) {
                rightOffset =  coords[0] + currentColumn - grid.length + 1;
            }
        }

        return new int[] {leftOffset, rightOffset};
    }

    private void tetronimoLanded() {
        int[] minMaxRows = addTetronimoToGrid(grid, currentRow, currentColumn, currentTetronimo);
        int minRow = minMaxRows[0];
        int maxRow = minMaxRows[1];
        pieceLanded = true;
        System.out.println("p");


        // Check if you are game over
        if (maxRow <= 3) {
            gameOver = true;
            stop();
        } else {

            // Remove any rotations of the tetronimo
            currentTetronimo.reset();
            getTetronimo();

            int fullRows = 0;
            // Check between the min and max row of the landed tetronimo if there is a full row
            for (int row = minRow; row <= maxRow; row++) {
                Boolean fullRow = true;
                for (int col = 0; col < grid.length; col++) {
                    if (grid[col][row] == -1) {
                        fullRow = false;
                        break;
                    }
                }

                // If there is a full row, remove it
                if (fullRow) {
                    fullRows += 1;
                    scoreBoard.addLine();
                    removeRow(row);
                }
            }

            scoreBoard.addScore(lineScore(scoreBoard.getLevel(), fullRows));
        }
    }

    public int[] addTetronimoToGrid(int[][] grid, int currentRow, int currentColumn, Tetronimo tetronimo) {
        // get rows tetronimo has landed
        int minRow = rows - 1;
        int maxRow = 0;

        // update grid with landed tetronimo
        for (int[] coords: tetronimo.pos) {
            if (currentRow >= 0) {
                grid[coords[0] + currentColumn][coords[1] + currentRow] = tetronimo.tetronimoType.ordinal();

                if (coords[1] + currentRow < minRow) {
                    minRow = coords[1] + currentRow;
                }

                if (coords[1] + currentRow > maxRow) {
                    maxRow = coords[1] + currentRow;
                }
            }
        }

        return new int[] {minRow, maxRow};
    }

    private void removeRow(int fullRow) {
        SoundPlayer.play("gem.wav", false);
        int centerCol = grid.length/2;
        for (int col = 0; col < grid.length/2; col++) {
            grid[centerCol - col - 1][fullRow] = -1;
            grid[centerCol + col][fullRow] = -1;

            repaint();
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                continue;
            }
        }


        for (int row = fullRow; row > 0; row--) {
            for (int col = 0; col < grid.length; col++) {
                grid[col][row] = grid[col][row - 1];
            }
        }
    }

    private void drawGridTetronimos(Graphics2D g) {
        if (paused) {
            g.setComposite(AlphaComposite.SrcOver.derive(0.15f));
        }
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != -1) {
                    drawRect(g, i, j - 2, tetronimoColors[grid[i][j]], tetronimoBorderColors[grid[i][j]]);
                }
            }
        }
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    private void drawGridLines(Graphics2D g) {
        // Set grid lines colors
        g.setColor(Color.gray);

        // Set opacity of grid lines
        g.setComposite(AlphaComposite.SrcOver.derive(0.15f));

        // Draw vertical gridlines
        for (int col = 0; col < grid.length; col++) {
            g.drawLine(col * rectSize, 0, col * rectSize, height);
        }

        // Draw horizontal gridlines
        for (int row = 0; row < grid[0].length; row++) {
            g.drawLine(0, row * rectSize, height/2, row * rectSize);
        }

        // Reset the opacity
        g.setComposite(AlphaComposite.SrcOver.derive(1f));

    }

    private void drawTetronimoDropPreview(Graphics2D g) {
        int row = currentRow;
        while(canMoveDown(grid, row, currentColumn, currentTetronimo)) {
            row += 1;
        }

        Color borderColor = tetronimoBorderColors[currentTetronimo.tetronimoType.ordinal()];

        for (int[] coords: currentTetronimo.pos) {
            drawRect(g, coords[0] + currentColumn, coords[1] + row - 2, Color.black, borderColor);
        }
    }


    // AI helper functions
    public int[][] getGrid() {
        return grid;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public Tetronimo getCurrentTetronimo() {
        return currentTetronimo;
    }

    public Tetronimo getNextTetronimo() {
        return nextTetronimo;
    }

    public Boolean getGameOver() {
        return gameOver;
    }

    public int getScore() {
        return scoreBoard.getScore();
    }

    public Boolean getPieceLanded() {
        return pieceLanded;
    }

    public void setPieceLanded(Boolean landed) {
        pieceLanded = landed;
    }
}
