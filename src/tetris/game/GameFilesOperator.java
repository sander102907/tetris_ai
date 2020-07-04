package tetris.game;

import java.io.*;

import static tetris.game.Config.gamefilePath;
import static tetris.game.Config.scoresFile;

public class GameFilesOperator {
    public static int getHighscore() {
        int highScore = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(gamefilePath + scoresFile)));
            String line = reader.readLine();
            while (line != null)                 // read the score file line by line
            {
                try {
                    int score = Integer.parseInt(line.trim());   // parse each line as an int
                    if (score > highScore)                       // and keep track of the largest
                    {
                        highScore = score;
                    }
                } catch (NumberFormatException e1) {
                    continue;
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException ex) {
            System.err.println("ERROR reading scores from file");
        }

        return highScore;

    }

    public static void setScore(int score) {
        // append the last score to the end of the file
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(new File(gamefilePath + scoresFile), true));
            output.newLine();
            output.append("" + score);
            output.close();

        } catch (IOException ex1) {
            System.out.printf("ERROR writing score to file: %s\n", ex1);
        }
    }


    public static void serializeGameState(Object state, String fileName) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(gamefilePath + fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(state);
            out.close();
            fileOut.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static Object deSerializeGameState(String fileName) {
        try {
            FileInputStream fileIn = new FileInputStream(gamefilePath + fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Object state = in.readObject();
            in.close();
            fileIn.close();

            return state;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return null;
        }
    }


}
