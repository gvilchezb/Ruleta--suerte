// java
package mikodes.watch.earns;

import android.content.Context;
import android.media.MediaPlayer;

public class BackgroundAudioManager {
    private static BackgroundAudioManager instance;
    private MediaPlayer bgPlayer;
    private MediaPlayer sfxPlayer;
    private boolean bgPausedBySfx = false;
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
        if (bgPlayer == null) {
            bgPlayer = MediaPlayer.create(appContext, R.raw.background);
            if (bgPlayer != null) {
                bgPlayer.setLooping(true);
                bgPlayer.setVolume(1f, 1f);
                bgPlayer.start();
            }
        }
    }

    // Reproducir un sfx desde res/raw (por ejemplo R.raw.button)
    public void playSound(int resId) {
        if (appContext == null) return;

        // liberar sfx anterior si existe
        if (sfxPlayer != null) {
            try { sfxPlayer.stop(); } catch (Exception ignored) {}
            try { sfxPlayer.release(); } catch (Exception ignored) {}
            sfxPlayer = null;
        }

        // pausar fondo si está sonando
        if (bgPlayer != null && bgPlayer.isPlaying()) {
            bgPlayer.pause();
            bgPausedBySfx = true;
        }

        sfxPlayer = MediaPlayer.create(appContext, resId);
        if (sfxPlayer == null) {
            // reanudar fondo si fallo el sfx
            if (bgPlayer != null && bgPausedBySfx) {
                bgPlayer.start();
                bgPausedBySfx = false;
            }
            return;
        }

        sfxPlayer.setOnCompletionListener(mp -> {
            try { mp.release(); } catch (Exception ignored) {}
            sfxPlayer = null;
            if (bgPlayer != null && bgPausedBySfx) {
                bgPlayer.start();
                bgPausedBySfx = false;
            }
        });

        sfxPlayer.setOnErrorListener((mp, what, extra) -> {
            try { mp.release(); } catch (Exception ignored) {}
            sfxPlayer = null;
            if (bgPlayer != null && bgPausedBySfx) {
                bgPlayer.start();
                bgPausedBySfx = false;
            }
            return true;
        });

        sfxPlayer.start();
    }

    // Pausar fondo (por ejemplo cuando la app entra a segundo plano)
    public void pauseBackground() {
        if (bgPlayer != null && bgPlayer.isPlaying()) {
            bgPlayer.pause();
            bgPausedBySfx = false;
        }
    }

    // Reanudar fondo (cuando la app vuelve a primer plano)
    public void resumeBackground() {
        if (bgPlayer != null && !bgPlayer.isPlaying()) {
            bgPlayer.start();
        }
    }

    // Liberar recursos (llamar en onTerminate o cuando se cierre la app)
    public void stopAll() {
        if (sfxPlayer != null) {
            try { sfxPlayer.stop(); } catch (Exception ignored) {}
            try { sfxPlayer.release(); } catch (Exception ignored) {}
            sfxPlayer = null;
        }
        if (bgPlayer != null) {
            try { bgPlayer.stop(); } catch (Exception ignored) {}
            try { bgPlayer.release(); } catch (Exception ignored) {}
            bgPlayer = null;
        }
        appContext = null;
        bgPausedBySfx = false;
    }
}
