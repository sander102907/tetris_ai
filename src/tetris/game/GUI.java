package tetris.game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static tetris.game.Config.*;


public class GUI extends JFrame {
    private JButton resumeSaveGame;
    private JButton start;
    private JButton resumeTwoPlayerSaveGame;
    private JButton startTwoPlayer;
    private JButton exit;
    private JButton resumeGame;
    private JButton quitToMenu;
    private Tetris[] games;
    private ScoreBoard[] scoreBoards;
    MenuPane menu;
    PauseMenu pauseMenu = new PauseMenu();


    public GUI() {
        setTitle("Tetris");
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Stop process on window close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        setLocationRelativeTo(null);

        setFocusable(true);
        requestFocusInWindow();

        JPanel container = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Image img = Toolkit.getDefaultToolkit().getImage("./resources/images/background.jpg");
                g.drawImage(img, 0, 0, this);
            }
        };

        container.setLayout(new BorderLayout());

        setContentPane(container);

        menu = new MenuPane();
        getContentPane().add(menu);
        pack();

        setVisible(true);

        start.addActionListener(e -> {
            container.setLayout(new FlowLayout());

            Tetris tetris = new Tetris();
            getContentPane().add(tetris);

            ScoreBoard scoreBoard = new ScoreBoard();
            tetris.setScoreBoard(scoreBoard);

            getContentPane().add(scoreBoard);

            getContentPane().remove(menu);

            games = new Tetris[]{tetris};
            scoreBoards = new ScoreBoard[]{scoreBoard};


            createKeyBindings(tetris, games, keyBindingsPlayers[0]);

            tetris.start();
        });

        resumeSaveGame.addActionListener(e -> {
            container.setLayout(new FlowLayout());

            // Get saved game states
            Tetris tetris = (Tetris) GameFilesOperator.deSerializeGameState(savegameFile);
            ScoreBoard scoreBoard = (ScoreBoard) GameFilesOperator.deSerializeGameState(
                    savegameScoreboardFile);

            getContentPane().add(tetris);

            tetris.setScoreBoard(scoreBoard);

            getContentPane().add(scoreBoard);

            getContentPane().remove(menu);

            games = new Tetris[]{tetris};
            scoreBoards = new ScoreBoard[]{scoreBoard};


            createKeyBindings(tetris, games, keyBindingsPlayers[0]);

            tetris.setPause();
            tetris.setPause();
        });


        startTwoPlayer.addActionListener(e -> {
            container.setLayout(new FlowLayout());

            Tetris tetris2 = new Tetris();
            getContentPane().add(tetris2);

            ScoreBoard scoreBoard2 = new ScoreBoard();
            tetris2.setScoreBoard(scoreBoard2);

            getContentPane().add(scoreBoard2);

            Tetris tetris = new Tetris();
            getContentPane().add(tetris);

            ScoreBoard scoreBoard = new ScoreBoard();
            tetris.setScoreBoard(scoreBoard);

            getContentPane().add(scoreBoard);

            getContentPane().remove(menu);

            games = new Tetris[]{tetris, tetris2};
            scoreBoards = new ScoreBoard[]{scoreBoard, scoreBoard2};

            createKeyBindings(tetris, games, keyBindingsPlayers[0]);
            createKeyBindings(tetris2, games, keyBindingsPlayers[1]);

            tetris.start();
            tetris2.start();
        });


        resumeTwoPlayerSaveGame.addActionListener(e -> {
            container.setLayout(new FlowLayout());

            games = new Tetris[2];
            scoreBoards = new ScoreBoard[2];

            for (int i = 1; i >= 0; i--) {
                // Get saved game states
                Tetris tetris = (Tetris) GameFilesOperator.deSerializeGameState(savegame2pFiles[i]);
                ScoreBoard scoreBoard = (ScoreBoard) GameFilesOperator.deSerializeGameState(
                        savegameScoreboard2pFiles[i]);

                getContentPane().add(tetris);

                tetris.setScoreBoard(scoreBoard);

                getContentPane().add(scoreBoard);

                getContentPane().remove(menu);

                games[i] = tetris;
                scoreBoards[i] = scoreBoard;


                createKeyBindings(tetris, games, keyBindingsPlayers[i]);

                tetris.setPause();
                tetris.setPause();
            }
        });

        exit.addActionListener(e -> System.exit(0));

        resumeGame.addActionListener(e -> {
            for (Tetris game: games) {
                game.setPause();

            }
            this.remove(pauseMenu);
            this.repaint();
        });

        quitToMenu.addActionListener(e -> {
            container.setLayout(new BorderLayout());

            for (int i = 0; i < games.length; i++) {
                Tetris game = games[i];
                game.setPause();
                game.stop();
                this.remove(game);

                // Save game state for possible resuming of save game
                if (games.length == 1) {
                    GameFilesOperator.serializeGameState(game, savegameFile);
                } else {
                    GameFilesOperator.serializeGameState(game, savegame2pFiles[i]);
                }
            }

            for (int i = 0; i < scoreBoards.length; i++ ) {
                this.remove(scoreBoards[i]);

                // Save game state for possible resuming of save game
                if (scoreBoards.length == 1) {
                    GameFilesOperator.serializeGameState(scoreBoards[i], savegameScoreboardFile);
                } else {
                    GameFilesOperator.serializeGameState(scoreBoards[i], savegameScoreboard2pFiles[i]);
                }
            }

            menu = new MenuPane();
            this.remove(pauseMenu);
            this.add(menu);
            this.repaint();

        });


        SoundPlayer soundPlayer = new SoundPlayer();
        soundPlayer.play("theme_song.wav", true);
    }

    // Create key bindings for controls
    private void createKeyBindings(Tetris tetris, Tetris[] games, String[] keyBindings) {
        InputMap im = tetris.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = tetris.getActionMap();

        im.put(KeyStroke.getKeyStroke(keyBindings[0]), MoveAction.Action.ROTATE);
        im.put(KeyStroke.getKeyStroke(keyBindings[1]), MoveAction.Action.SOFTDROP);
        im.put(KeyStroke.getKeyStroke(keyBindings[2]), MoveAction.Action.MOVE_LEFT);
        im.put(KeyStroke.getKeyStroke(keyBindings[3]), MoveAction.Action.MOVE_RIGHT);
        im.put(KeyStroke.getKeyStroke("released " + keyBindings[1]), MoveAction.Action.STOPSOFTDROP);
        im.put(KeyStroke.getKeyStroke(keyBindings[4]), MoveAction.Action.HARDDROP);
        im.put(KeyStroke.getKeyStroke(keyBindings[5]), "PAUSE");

        am.put(MoveAction.Action.ROTATE, new MoveAction(this, MoveAction.Action.ROTATE, tetris));
        am.put(MoveAction.Action.SOFTDROP, new MoveAction(this, MoveAction.Action.SOFTDROP, tetris));
        am.put(MoveAction.Action.MOVE_LEFT, new MoveAction(this, MoveAction.Action.MOVE_LEFT, tetris));
        am.put(MoveAction.Action.MOVE_RIGHT, new MoveAction(this, MoveAction.Action.MOVE_RIGHT, tetris));
        am.put(MoveAction.Action.STOPSOFTDROP, new MoveAction(this, MoveAction.Action.STOPSOFTDROP, tetris));
        am.put(MoveAction.Action.HARDDROP, new MoveAction(this, MoveAction.Action.HARDDROP, tetris));
        am.put("PAUSE", new PauseAction(this, games, pauseMenu));
    }

    public class MenuPane extends JPanel {

        public MenuPane() {
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.NORTH;

            setBackground(new Color(0,0,0,0));

            JLabel title = new JLabel("<html><h1><strong><i>Tetris</i></strong></h1><hr></html>");
            title.setForeground(Color.white);
            add(title, gbc);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(3, 3, 3, 3);

            JPanel buttons = new JPanel(new GridBagLayout());

            if (resumeSaveGame == null) {
                resumeSaveGame = new JButton("Resume game");
                resumeSaveGame.setFont(new Font("century", Font.PLAIN, 20));
            }

            if (start == null) {
                start = new JButton("New game");
                start.setFont(new Font("century", Font.PLAIN, 20));
            }

            if (resumeTwoPlayerSaveGame == null) {
                resumeTwoPlayerSaveGame = new JButton("Resume 2 player game");
                resumeTwoPlayerSaveGame.setFont(new Font("century", Font.PLAIN, 20));
            }

            if (startTwoPlayer == null) {
                startTwoPlayer = new JButton("New 2 player game");
                startTwoPlayer.setFont(new Font("century", Font.PLAIN, 20));
            }

            if (exit == null) {
                exit = new JButton("Exit");
                exit.setFont(new Font("century", Font.PLAIN, 20));
            }


            // Check if there are save games available before adding the button to the menu
            if (new File(gamefilePath + savegameFile).isFile() &&
                    new File(gamefilePath + savegameScoreboardFile).isFile()) {
                buttons.add(resumeSaveGame, gbc);
            }

            buttons.add(start, gbc);

            // Check if there are save games available before adding the button to the menu
            if (new File(gamefilePath + savegame2pFiles[0]).isFile() &&
                    new File(gamefilePath + savegame2pFiles[1]).isFile() &&
                    new File(gamefilePath + savegameScoreboard2pFiles[0]).isFile() &&
                    new File(gamefilePath + savegameScoreboard2pFiles[1]).isFile()) {
                buttons.add(resumeTwoPlayerSaveGame, gbc);
            }

            buttons.add(startTwoPlayer, gbc);
            buttons.add(exit, gbc);




            buttons.setBackground(new Color(0, 0, 0, 0));

            gbc.weighty = 1;
            add(buttons, gbc);

        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Image img = Toolkit.getDefaultToolkit().getImage("./resources/images/background.jpg");
            g.drawImage(img, 0, 0, this);
        }

    }

    public class PauseMenu extends JPanel {

        public PauseMenu() {
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.NORTH;

            setBackground(new Color(0,0,0,200));

            JLabel title = new JLabel(" Game paused ");
            title.setForeground(Color.white);
            title.setFont(new Font("century", Font.BOLD, 30));
            add(title, gbc);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(3, 3, 3, 3);

            resumeGame = new JButton("Resume");
            resumeGame.setFont(new Font("century", Font.PLAIN, 20));


            quitToMenu = new JButton("Save and quit to menu");
            quitToMenu.setFont(new Font("century", Font.PLAIN, 20));

            JPanel buttons = new JPanel(new GridBagLayout());
            buttons.add(resumeGame, gbc);
            buttons.add(quitToMenu, gbc);

            buttons.setBackground(new Color(0, 0, 0, 0));

            gbc.weighty = 1;
            add(buttons, gbc);

        }

    }


    public static void main(String[] args) {
        GUI gui = new GUI();
    }
}


// Handles the key bindings
class MoveAction extends AbstractAction {

    enum Action {
        ROTATE, SOFTDROP, MOVE_LEFT, MOVE_RIGHT, HARDDROP, STOPSOFTDROP;
    }

    Window window;
    Action action;
    Tetris tetris;

    public MoveAction(Window window, Action action, Tetris tetris) {
        this.window = window;
        this.action = action;
        this.tetris = tetris;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!tetris.getPause()) {
            switch (action) {
                case ROTATE:
                    tetris.rotateTetronimo();
                    break;
                case SOFTDROP:
                    tetris.moveDown();
                    break;
                case MOVE_LEFT:
                    tetris.moveLeft();
                    break;
                case MOVE_RIGHT:
                    tetris.moveRight();
                    break;
                case HARDDROP:
                    tetris.hardDrop();
                    break;
                case STOPSOFTDROP:
                    tetris.setSoftDrop(false);
                    break;
            }
        }
    }
}

class PauseAction extends AbstractAction {
    private GUI.PauseMenu menu;
    private Tetris[] games;
    private Window window;

    public PauseAction(Window window, Tetris[] games, GUI.PauseMenu menu) {
        this.menu = menu;
        this.games = games;
        this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Tetris game: games) {
            game.setPause();
        }

        if (games[0].getPause()) {
            window.add(menu);
        } else {
            window.remove(menu);
        }
        window.repaint();
        window.setVisible(true);
    }
}
