package mikodes.watch.earns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferences prefs = base.getSharedPreferences("ajustes", MODE_PRIVATE);
        String lang = prefs.getString("idioma", "es");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        super.attachBaseContext(base.createConfigurationContext(config));
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        // Botón Jugar
        findViewById(R.id.btnJugar).setOnClickListener(v -> {
            // Reproducir sonido del botón
            BackgroundAudioManager.getInstance().playButtonSound();

            // Abrir la pantalla de instrucciones
            Intent intent = new Intent(SplashScreen.this, Instructions.class);
            startActivity(intent);
            finish();
        });

        // Botón Marcadores
        findViewById(R.id.marcadores).setOnClickListener(v -> {
            // Reproducir sonido del botón
            BackgroundAudioManager.getInstance().playButtonSound();

            // Abrir la pantalla de marcadores
            Intent intent = new Intent(SplashScreen.this, Marcadores.class);
            startActivity(intent);
            finish();
        });
        // Botón Información

        findViewById(R.id.WebView).setOnClickListener(v -> {

            // Reproducir sonido del botón
            BackgroundAudioManager.getInstance().playButtonSound();

            // Abrir la pantalla de información)
            Intent intent = new Intent(SplashScreen.this, InfoWebActivity.class);
                startActivity(intent);
            });
        // Botón Opciones

        findViewById(R.id.Options).setOnClickListener(v -> {

            // Reproducir sonido del botón
            BackgroundAudioManager.getInstance().playButtonSound();

            // Abrir la pantalla de Opciones)
            Intent intent = new Intent(SplashScreen.this, Options.class);
            startActivity(intent);
        });

    }
}