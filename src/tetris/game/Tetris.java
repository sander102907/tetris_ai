package tetris.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.Timer;

import static tetris.game.Config.*;
import static tetris.game.Draw.drawRect;

public class Tetris extends JPanel implements Runnable {
    static final Random rand = new Random();
    private int currentRow;
    private int currentColumn;
    private int[][] grid = new int[columns][rows];
    private Tetronimo currentTetronimo;
    private Tetronimo nextTetronimo;
    private Boolean softDrop = false;
    private Boolean hardDrop = false;

    private Set<Integer>  randomTetronimos;

    Timer timer;
    Thread gravityThread;
    private SoundPlayer soundPlayer;
    private ScoreBoard scoreBoard;


    public Tetris() {
        setPreferredSize(new Dimension(height/2, height));
        setBorder(BorderFactory.createLineBorder(Color.white));
        setBackground(Color.black);
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (canMoveLeft()) {
                            currentColumn--;
                            soundPlayer.play("move.wav", false);
                        }
                        break;

                    case KeyEvent.VK_RIGHT:
                        if (canMoveRight()) {
                            currentColumn++;
                            soundPlayer.play("move.wav", false);
                        }
                        break;

                    case KeyEvent.VK_UP:
                        if (canRotate()) {
                            rotate();
                            soundPlayer.play("rotate.wav", false);
                        }
                        break;

                    case KeyEvent.VK_DOWN:
                        if (!softDrop) {
                            softDrop = true;
                            gravityThread.interrupt();
                        }
                        break;

                    case KeyEvent.VK_SPACE:
                        while(canMoveDown(currentRow)) {
                            currentRow += 1;
                            scoreBoard.addScore(2);
                        }
                        hardDrop = true;
//                        tetronimoLanded();
                        break;
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                softDrop = false;
            }
        });
    }

    private void getTetronimo() {
        currentTetronimo = nextTetronimo;
        nextTetronimo = getRandomTetronimo();
        scoreBoard.drawPreviewTetronimo(nextTetronimo);
        currentRow = -1;
        currentColumn = 5;
    }


    // Random Generator generates a sequence of all seven one-sided tetrominoes (I, J, L, O, S, T, Z) permuted
    // randomly, as if they were drawn from a bag. Then it deals all seven tetrominoes to the piece sequence
    // before generating another bag.
    private Tetronimo getRandomTetronimo() {
        if (randomTetronimos == null || randomTetronimos.isEmpty()) {
            randomTetronimos = new LinkedHashSet<>();
            while (randomTetronimos.size() < Tetronimo.values().length) {
                randomTetronimos.add(rand.nextInt(Tetronimo.values().length));
            }
        }

        Iterator<Integer> it = randomTetronimos.iterator();
        int randomTetronimo = it.next();
        it.remove();

        return Tetronimo.values()[randomTetronimo];
    }

    private void start() {
        initGrid();

        timer = new Timer(true);
        timer.schedule(new GameTimer(scoreBoard), 0, 1000);


        nextTetronimo = getRandomTetronimo();
        softDrop = false;
        getTetronimo();

        gravityThread = new Thread(this);
        gravityThread.start();

        soundPlayer = new SoundPlayer();

    }


    private void stop() {
        if (gravityThread != null) {
            Thread temp = gravityThread;
            gravityThread = null;
            temp.interrupt();
        }
    }

    @Override
    public void run() {
        while (Thread.currentThread() == gravityThread) {
            try {
                if (softDrop) {
                    Thread.sleep(softDropSpeed);
                    scoreBoard.addScore(1);
                    soundPlayer.play("softdrop.wav", false);
                } else {
                    Thread.sleep((int) levelSpeeds[scoreBoard.getLevel()]);
                }

            } catch (InterruptedException e) {
                continue;
            }

            if (canMoveDown(currentRow)) {
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

                soundPlayer.play("lock.wav", false);
                if (canMoveDown(currentRow)) {
                    currentRow += 1;
                } else {
                    tetronimoLanded();
                    soundPlayer.play("lock.wav", false);
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
        Color color = tetronimoColors[tetronimo.ordinal()];
        Color borderColor = tetronimoBorderColors[tetronimo.ordinal()];

        for (int[] coords: tetronimo.pos) {
            drawRect(g, coords[0] + currentColumn, coords[1] + currentRow, color, borderColor);
        }
    }

    private void initGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = -1;
            }
        }
    }

    private Boolean canMoveDown(int currentRow) {
        for (int[] coords: currentTetronimo.pos) {
            if (
                    currentRow + coords[1] + 1 >= 0 &&
                    currentColumn + coords[0] < grid.length &&
                    currentColumn + coords[0] >= 0 &&
                    (currentRow + coords[1] >= grid[0].length - 1 ||
                    grid[currentColumn + coords[0]][currentRow + coords[1] + 1] != -1)
            ) {
                return false;
            }
        }
        return true;
    }

    private Boolean canMoveLeft() {
        for (int[] coords: currentTetronimo.pos) {
            if (
                    currentRow + coords[1] >= 0 &&
                    (currentColumn + coords[0] <= 0 ||
                    grid[currentColumn + coords[0] -1][currentRow + coords[1]] != -1)
            ) {
                return false;
            }
        }
        return true;
    }

    private Boolean canRotate() {
        int[] offsets = getRotationOffsets();


        for (int[] coords: currentTetronimo.pos) {
            if (
                    currentRow + coords[0] >= 0 &&
                    grid[-coords[1] + currentColumn - offsets[0] - offsets[1]][coords[0] + currentRow] != -1
            ) {
                return false;
            }
        }

        currentColumn -= offsets[0] + offsets[1];
        return true;
    }

    private Boolean canMoveRight() {
        for (int[] coords: currentTetronimo.pos) {
            if (
                    currentRow + coords[1] >= 0 &&
                    (currentColumn + coords[0] >= grid.length - 1 ||
                    grid[currentColumn + coords[0] + 1][currentRow + coords[1]] != -1)
            ) {
                return false;
            }
        }
        return true;
    }

    public int[] getRotationOffsets() {
        int rightOffset = 0;
        int leftOffset = 0;

        for (int[] coords: currentTetronimo.pos) {
            // If after rotations, will be out of grid on left side
            if (-coords[1] + currentColumn < leftOffset) {
                leftOffset = -coords[1] + currentColumn;
                // If after rotations, will be out of grid on right side
            } else if (-coords[1] + currentColumn - grid.length + 1 > rightOffset) {
                rightOffset =  -coords[1] + currentColumn - grid.length + 1;
            }
        }

        return new int[] {leftOffset, rightOffset};
    }

    public void rotate() {
        if (currentTetronimo != Tetronimo.O_Tetronimo) {
            // Clockwise rotation
            for (int[] coords : currentTetronimo.pos) {
                int tmp = coords[0];
                coords[0] = -coords[1];
                coords[1] = tmp;
            }
        }
    }

    private void tetronimoLanded() {
        // get rows tetronimo has landed
        int minRow = rows - 1;
        int maxRow = 0;

        // update grid with landed tetronimo
        for (int[] coords: currentTetronimo.pos) {
            grid[coords[0] + currentColumn][coords[1] + currentRow] = currentTetronimo.ordinal();

            if (coords[1] + currentRow < minRow) {
                minRow = coords[1] + currentRow;
            }

            if (coords[1] + currentRow > maxRow) {
                maxRow = coords[1] + currentRow;
            }
        }

        // Check if you are game over
        if (currentRow < 1) {
            stop();
        }

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

    private void removeRow(int fullRow) {
        soundPlayer.play("gem.wav", false);
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
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != -1) {
                    drawRect(g, i, j, tetronimoColors[grid[i][j]], tetronimoBorderColors[grid[i][j]]);
                }
            }
        }
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
        start();
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
        while(canMoveDown(row)) {
            row += 1;
        }

        Color borderColor = tetronimoBorderColors[currentTetronimo.ordinal()];

        for (int[] coords: currentTetronimo.pos) {
            drawRect(g, coords[0] + currentColumn, coords[1] + row, Color.black, borderColor);
        }
    }


}
