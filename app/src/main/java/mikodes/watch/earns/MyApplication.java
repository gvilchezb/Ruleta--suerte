// java
package mikodes.watch.earns;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Locale;

public class MyApplication extends Application {
    private int startedActivities = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar el manager primero
        BackgroundAudioManager.getInstance().init(this);

// Aplicar música personalizada si existe
        SharedPreferences prefs = getSharedPreferences("ajustes", MODE_PRIVATE);
        String musicaUri = prefs.getString("musica_personalizada", null);

        if (musicaUri != null) {
            BackgroundAudioManager.getInstance().setCustomBackgroundMusic(Uri.parse(musicaUri));
        }
// Si no hay música personalizada, ya se está reproduciendo la música por defecto dentro de init()



        // Inicializar el manager con applicationContext
        BackgroundAudioManager.getInstance().init(this);

        // Control simple del ciclo de vida para pausar/reanudar música cuando la app entra/sale de segundo plano
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
            @Override public void onActivityStarted(Activity activity) {
                startedActivities++;
                if (startedActivities == 1) {
                    // la app volvió a primer plano
                    BackgroundAudioManager.getInstance().resumeBackground();
                }
            }
            @Override public void onActivityResumed(Activity activity) {}
            @Override public void onActivityPaused(Activity activity) {}
            @Override public void onActivityStopped(Activity activity) {
                startedActivities = Math.max(0, startedActivities - 1);
                if (startedActivities == 0) {
                    // la app quedó en segundo plano
                    BackgroundAudioManager.getInstance().pauseBackground();
                }
            }
            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
            @Override public void onActivityDestroyed(Activity activity) {}



        });
    }
}
