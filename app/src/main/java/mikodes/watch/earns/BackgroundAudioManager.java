
package mikodes.watch.earns;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class BackgroundAudioManager {
    private static BackgroundAudioManager instance;

    private MediaPlayer bgPlayer;
    private SoundPool soundPool;
    private int buttonSoundId;
    private int wheelSoundId;
    private int victorySoundId;
    private Context appContext;

    private BackgroundAudioManager() {}

    public static synchronized BackgroundAudioManager getInstance() {
        if (instance == null) instance = new BackgroundAudioManager();
        return instance;
    }

    // Inicializar con applicationContext (llamar desde Application.onCreate)
    public void init(Context context) {
        if (context == null) return;
        if (appContext == null) appContext = context.getApplicationContext();

        // Inicializar música de fondo
        if (bgPlayer == null) {
            bgPlayer = MediaPlayer.create(appContext, R.raw.background);
            if (bgPlayer != null) {
                bgPlayer.setLooping(true);
                bgPlayer.setVolume(1f, 1f);
                bgPlayer.start();
            }
        }

        // Inicializar SoundPool para efectos
        if (soundPool == null) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(attrs)
                    .build();

            // Cargar sonidos
            buttonSoundId = soundPool.load(appContext, R.raw.button, 1);
            wheelSoundId = soundPool.load(appContext, R.raw.wheel, 1);
            victorySoundId = soundPool.load(appContext, R.raw.victory, 1);

        }
    }

    // Reproducir sonido del botón
    public void playButtonSound() {
        playSfx(buttonSoundId);
    }

    // Reproducir sonido de éxito
    public void playWheelSound() {
        playSfx(wheelSoundId);
    }

    // Reproducir sonido de error
    public void playVictorySound() {
        playSfx(victorySoundId);
    }

    // Método interno para reproducir SFX y bajar volumen del fondo temporalmente
    private void playSfx(int soundId) {
        if (soundPool != null && soundId != 0) {
            // Bajar volumen del fondo mientras suena el efecto
            if (bgPlayer != null && bgPlayer.isPlaying()) {
                bgPlayer.setVolume(0.3f, 0.3f);
            }

            soundPool.play(soundId, 1f, 1f, 1, 0, 1f);

            // Restaurar volumen del fondo después de un breve delay
            new android.os.Handler().postDelayed(() -> {
                if (bgPlayer != null && bgPlayer.isPlaying()) {
                    bgPlayer.setVolume(1f, 1f);
                }
            }, 500); // medio segundo
        }
    }

    // Pausar música de fondo
    public void pauseBackground() {
        if (bgPlayer != null && bgPlayer.isPlaying()) {
            bgPlayer.pause();
        }
    }

    // Reanudar música de fondo
    public void resumeBackground() {
        if (bgPlayer != null && !bgPlayer.isPlaying()) {
            bgPlayer.start();
        }
    }

    // Liberar recursos (llamar en onTerminate o cuando se cierre la app)
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
