package tetris.game;

import javax.sound.sampled.*;
import java.io.File;
import static tetris.game.Config.soundPath;

public class SoundPlayer {
    public static void play(String path, Boolean loop) {
        File file = new File(soundPath + path);
        try {
            final Clip clip = (Clip)AudioSystem.getLine(new Line.Info(Clip.class));

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP)
                    clip.close();
            });

            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }
}
