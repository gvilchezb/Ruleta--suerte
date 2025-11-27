package mikodes.watch.earns;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
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
    }
}