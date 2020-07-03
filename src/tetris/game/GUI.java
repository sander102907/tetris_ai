package tetris.game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private JButton start;
    private JButton startTwoPlayer;
    private JButton scoresButton;
    private JButton helpButton;
    private JButton exit;

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

        MenuPane menu = new MenuPane();
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


            createKeyBindings(tetris, "UP", "DOWN", "LEFT", "RIGHT", "SPACE");
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

            createKeyBindings(tetris, "UP", "DOWN", "LEFT", "RIGHT", "SPACE");
            createKeyBindings(tetris2, "W", "S", "A", "D", "R");
        });

        exit.addActionListener(e -> System.exit(0));


        SoundPlayer soundPlayer = new SoundPlayer();
        soundPlayer.play("theme_song.wav", true);
    }

    // Create key bindings for controls
    private void createKeyBindings(Tetris tetris, String up, String down, String left, String right, String hardDrop) {
        InputMap im = tetris.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = tetris.getActionMap();
        im.put(KeyStroke.getKeyStroke(up), MoveAction.Action.ROTATE);
        im.put(KeyStroke.getKeyStroke(down), MoveAction.Action.SOFTDROP);
        im.put(KeyStroke.getKeyStroke(left), MoveAction.Action.MOVE_LEFT);
        im.put(KeyStroke.getKeyStroke(right), MoveAction.Action.MOVE_RIGHT);
        im.put(KeyStroke.getKeyStroke("released " + down), MoveAction.Action.STOPSOFTDROP);
        im.put(KeyStroke.getKeyStroke(hardDrop), MoveAction.Action.HARDDROP);
        am.put(MoveAction.Action.ROTATE, new MoveAction(this, MoveAction.Action.ROTATE, tetris));
        am.put(MoveAction.Action.SOFTDROP, new MoveAction(this, MoveAction.Action.SOFTDROP, tetris));
        am.put(MoveAction.Action.MOVE_LEFT, new MoveAction(this, MoveAction.Action.MOVE_LEFT, tetris));
        am.put(MoveAction.Action.MOVE_RIGHT, new MoveAction(this, MoveAction.Action.MOVE_RIGHT, tetris));
        am.put(MoveAction.Action.STOPSOFTDROP, new MoveAction(this, MoveAction.Action.STOPSOFTDROP, tetris));
        am.put(MoveAction.Action.HARDDROP, new MoveAction(this, MoveAction.Action.HARDDROP, tetris));
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

            start = new JButton("Start");
            start.setFont(new Font("century", Font.PLAIN, 20));

            startTwoPlayer = new JButton("Start 2 player game");
            startTwoPlayer.setFont(new Font("century", Font.PLAIN, 20));

            scoresButton = new JButton("Show scores");
            scoresButton.setFont(new Font("century", Font.PLAIN, 20));

            helpButton = new JButton("Help");
            helpButton.setFont(new Font("century", Font.PLAIN, 20));

            exit = new JButton("Exit");
            exit.setFont(new Font("century", Font.PLAIN, 20));

            JPanel buttons = new JPanel(new GridBagLayout());
            buttons.add(start, gbc);
            buttons.add(startTwoPlayer, gbc);
            buttons.add(scoresButton, gbc);
            buttons.add(helpButton, gbc);
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
        }
    }
}
