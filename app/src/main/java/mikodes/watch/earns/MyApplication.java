// java
package mikodes.watch.earns;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class MyApplication extends Application {
    private int startedActivities = 0;

    @Override
    public void onCreate() {
        super.onCreate();
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
