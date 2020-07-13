package tetris.ai;

import tetris.game.Tetris;
import tetris.game.Tetronimo;

import java.util.Dictionary;
import java.util.Hashtable;

public class AI implements Runnable {
    private Tetris tetris;
    private int[][] grid;
    private int currentColumn;
    private int currentRow;
    private Thread AIThread;
    private Tetronimo currentTetronimo;
    private Tetronimo nextTetronimo;
    private int bestScore;
    private Boolean movesFound;


    public AI(Tetris tetris) {
        this.tetris = tetris;
    }

    public void start() {
        movesFound = false;
        AIThread = new Thread(this);
        AIThread.start();
        tetris.setSoftDrop(true);
    }

    private Dictionary getNextMoves() {
        bestScore = -999999999;
        Dictionary moves = new Hashtable();
        moves.put("rotations", 0);
        moves.put("left", 0);
        moves.put("right", 0);

        int left = 0;
        int right = 0;
        int down = 0;

        moves = testLeftMoves(moves, left, right, down, 0);
        moves = testRightMoves(moves, left, right, down, 0);

        for (int i = 0; i < 4; i++) {
            if (tetris.canRotate(
                    grid, currentRow + down, currentColumn + right - left, currentTetronimo)) {
                currentTetronimo.rotate();

                moves = testLeftMoves(moves, left, right, down, i);
                moves = testRightMoves(moves, left, right, down, i);
            }
        }

        return moves;
    }

    private Dictionary testLeftMoves(Dictionary moves, int left, int right, int down, int rotations) {
        while(tetris.canMoveLeft(
                grid, currentRow + down, currentColumn + right - left, currentTetronimo)) {
            left++;
            while(tetris.canMoveDown(
                    grid, currentRow + down, currentColumn + right - left, currentTetronimo)) {
                down++;
            }

            moves = calculateScore(down, moves, left, right, rotations);
            down = 0;
        }

        left = 0;

        return moves;
    }

    private Dictionary testRightMoves(Dictionary moves, int left, int right, int down, int rotations) {
        while(tetris.canMoveRight(
                grid, currentRow + down, currentColumn + right - left, currentTetronimo)) {
            right++;
            while(tetris.canMoveDown(
                    grid, currentRow + down, currentColumn + right - left, currentTetronimo)) {
                down++;
            }

            moves = calculateScore(down, moves, left, right, rotations);
            down = 0;
        }

        right = 0;

        return moves;
    }

    private Dictionary calculateScore(int down, Dictionary moves, int left, int right, int rotations) {
        int[] minMaxRows = tetris.addTetronimoToGrid(
                grid, currentRow + down, currentColumn + right - left, currentTetronimo);
        int minRow = minMaxRows[0];
        int maxRow = minMaxRows[1];

//        System.out.println("minRow: " + minRow);
//        System.out.println("maxRow: " + maxRow);

        int newScore = tetris.getScore() + down + ((21 - maxRow) * 2);

//        System.out.println("newscore: " + newScore);

        if (newScore > bestScore) {
            bestScore = newScore;
//            System.out.println("bestScore: " + bestScore);
            moves.put("rotations", rotations);
            moves.put("left", left);
            moves.put("right", right);
        }

        return moves;
    }

    private void executeMoves(Dictionary moves) {
        // Execute rotations
        for (int i = 0; i < (int) moves.get("rotations"); i++) {
            tetris.rotateTetronimo();
        }

        // Execute left moves
        for (int i = 0; i < (int) moves.get("left"); i++) {
            tetris.moveLeft();
        }

        // Execute right moves
        for (int i = 0; i < (int) moves.get("right"); i++) {
            tetris.moveRight();
        }
    }




    @Override
    public void run() {
        while (!tetris.getGameOver() && Thread.currentThread() == AIThread) {

            Boolean pieceLanded = tetris.getPieceLanded();
            System.out.println(pieceLanded);

            if (pieceLanded) {
                System.out.println("yes");
                tetris.setPieceLanded(false);
                movesFound = false;
            }
            if (!movesFound) {
                // Create deep copy of grid
                grid = new int[tetris.getGrid().length][];
                for (int i = 0; i < tetris.getGrid().length; i++) {
                    grid[i] = tetris.getGrid()[i].clone();
                }

                currentColumn = tetris.getCurrentColumn();
                currentRow = tetris.getCurrentRow();

                // Create deep copies of the tetronimos
                currentTetronimo = new Tetronimo(tetris.getCurrentTetronimo().getTetronimoType());
                nextTetronimo = new Tetronimo(tetris.getNextTetronimo().getTetronimoType());

                Dictionary moves = getNextMoves();
                executeMoves(moves);
                movesFound = true;
            }
        }
    }




}
