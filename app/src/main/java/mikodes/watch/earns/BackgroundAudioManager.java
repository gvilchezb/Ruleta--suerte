package mikodes.watch.earns;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;

public class BackgroundAudioManager {
    private static BackgroundAudioManager instance;

    private MediaPlayer bgPlayer;   // 🔥 ÚNICO reproductor de música de fondo
    private SoundPool soundPool;

    private int buttonSoundId;
    private int wheelSoundId;
    private int victorySoundId;

    private Context appContext;

    private String customMusicUri = null;

    private BackgroundAudioManager() {}

    public static synchronized BackgroundAudioManager getInstance() {
        if (instance == null) instance = new BackgroundAudioManager();
        return instance;
    }

    // Inicializar (llamar desde Application)
    public void init(Context context) {
        if (context == null) return;

        if (appContext == null) appContext = context.getApplicationContext();

        // Crear SoundPool (SFX)
        if (soundPool == null) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(attrs)
                    .build();

            buttonSoundId = soundPool.load(appContext, R.raw.button, 1);
            wheelSoundId = soundPool.load(appContext, R.raw.wheel, 1);
            victorySoundId = soundPool.load(appContext, R.raw.victory, 1);
        }

        // Crear música de fondo por defecto
        if (bgPlayer == null) {
            bgPlayer = MediaPlayer.create(appContext, R.raw.background);
            if (bgPlayer != null) {
                bgPlayer.setLooping(true);
                bgPlayer.setVolume(1f, 1f);
                bgPlayer.start();
            }
        }
    }

    // ------- MÚSICA PERSONALIZADA --------

    public void setCustomBackgroundMusic(Uri uri) {
        try {
            if (bgPlayer != null) {
                bgPlayer.reset();
            } else {
                bgPlayer = new MediaPlayer();
            }

            bgPlayer.setDataSource(appContext, uri);
            bgPlayer.setLooping(true);
            bgPlayer.prepare();
            bgPlayer.start();

            customMusicUri = uri.toString(); // por si luego quieres guardarla

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Guardar si hay música personalizada
    public boolean hasCustomMusic() {
        return customMusicUri != null;
    }

    public String getCustomMusicUri() {
        return customMusicUri;
    }

    // Volver a la música original de la app
    public void playOriginalMusic() {
        try {
            if (bgPlayer != null) {
                bgPlayer.reset();
            } else {
                bgPlayer = new MediaPlayer();
            }

            // Reproducir música por defecto
            bgPlayer = MediaPlayer.create(appContext, R.raw.background);
            bgPlayer.setLooping(true);
            bgPlayer.setVolume(1f, 1f);
            bgPlayer.start();

            // Limpiar música personalizada
            customMusicUri = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ------- EFECTOS -------
    public void playButtonSound() { playSfx(buttonSoundId); }
    public void playWheelSound() { playSfx(wheelSoundId); }
    public void playVictorySound() { playSfx(victorySoundId); }

    private void playSfx(int soundId) {
        if (soundPool != null && soundId != 0) {
            if (bgPlayer != null && bgPlayer.isPlaying())
                bgPlayer.setVolume(0.3f, 0.3f);

            soundPool.play(soundId, 1f, 1f, 1, 0, 1f);

            new android.os.Handler().postDelayed(() -> {
                if (bgPlayer != null && bgPlayer.isPlaying())
                    bgPlayer.setVolume(1f, 1f);
            }, 500);
        }
    }

    // ------- CONTROL --------
    public void pauseBackground() {
        if (bgPlayer != null && bgPlayer.isPlaying())
            bgPlayer.pause();
    }

    public void resumeBackground() {
        if (bgPlayer != null && !bgPlayer.isPlaying())
            bgPlayer.start();
    }

    // ------- LIMPIEZA --------
    public void stopAll() {
        if (bgPlayer != null) {
            try { bgPlayer.stop(); } catch (Exception ignored) {}
            try { bgPlayer.release(); } catch (Exception ignored) {}
            bgPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        appContext = null;
    }
}
