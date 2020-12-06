package v3;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicMain {

    public static void main(String[] args) {

        new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(Paths.get("src/main/resources/alarmSound1.mp3").toUri().getPath());
                AdvancedPlayer player = new AdvancedPlayer(fis);

                player.play();
                // Open an audio input stream.
                player.stop();
            } catch (IOException e) {
                System.err.println("Unable to create temporary audio file, message="+e.getMessage());
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
