package tetris.game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

public class AudioManager {
    private MediaPlayer musicPlayer;
    private MediaPlayer clearPlayer;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    private double musicVolume = 0.25;
    private double clearVolume = 1.0;

    public AudioManager() {
        URL musicUrl = findResource("/audio/music.wav", "/audio/music.mp3");
        URL clearUrl = findResource("/audio/clear.wav", "/audio/clear.mp3");

        if (musicUrl != null) {
            musicPlayer = new MediaPlayer(new Media(musicUrl.toExternalForm()));
            musicPlayer.setVolume(musicVolume);
        }
        if (clearUrl != null) {
            clearPlayer = new MediaPlayer(
                    new Media(clearUrl.toExternalForm()));
            clearPlayer.setVolume(clearVolume);
        }
    }

    private URL findResource(String... resourceNames) {
        for (String resourceName : resourceNames) {
            URL resource = getClass().getResource(resourceName);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    public void playMusic() {
        if (!musicEnabled || musicPlayer == null) {
            return;
        }
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        if (musicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            musicPlayer.play();
        }
    }

    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (enabled) {
            playMusic();
        } else {
            stopMusic();
        }
    }

    public void setSfxEnabled(boolean enabled) {
        sfxEnabled = enabled;
        if (!enabled && clearPlayer != null) {
            clearPlayer.stop();
        }
    }

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    public void playClearSound() {
        if (!sfxEnabled || clearPlayer == null) {
            return;
        }
        clearPlayer.stop();
        clearPlayer.setVolume(clearVolume);
        clearPlayer.seek(Duration.ZERO);
        clearPlayer.play();
    }

    public void dispose() {
        stopMusic();
        if (musicPlayer != null) {
            musicPlayer.dispose();
            musicPlayer = null;
        }
        if (clearPlayer != null) {
            clearPlayer.stop();
            clearPlayer.dispose();
            clearPlayer = null;
        }
    }
}