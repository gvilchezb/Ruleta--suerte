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
            Intent intent = new Intent(SplashScreen.this, Instructions.class);
            startActivity(intent);
            finish();
        });

        // Botón Marcadores
        findViewById(R.id.marcadores).setOnClickListener(v -> {
            Intent intent = new Intent(SplashScreen.this, Marcadores.class);
            startActivity(intent);
            finish();
        });
    }
}