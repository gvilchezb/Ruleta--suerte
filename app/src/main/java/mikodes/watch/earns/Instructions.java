package mikodes.watch.earns;

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



public class Instructions extends AppCompatActivity{

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
        setContentView(R.layout.activity_instructions);

        // Referencia al botón
        Button btnVolver = findViewById(R.id.btnJugar1);

        // Listener para abrir la pantalla de la ruleta
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Reproducir sonido del botón
                BackgroundAudioManager.getInstance().playButtonSound();

                //Abrir la pantalla de la ruleta
                Intent intent = new Intent(Instructions.this, LuckyWheel.class);
                startActivity(intent);
                finish(); // Cierra la pantalla de instrucciones
            }
        });
    }


}
