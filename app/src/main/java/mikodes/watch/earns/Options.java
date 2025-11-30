package mikodes.watch.earns;

import static android.content.Context.MODE_PRIVATE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class Options extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_options);

        // ---- BOTONES DE IDIOMA ----
        findViewById(R.id.btnIdiomaEspañol).setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().playButtonSound();
            cambiarIdioma("es");
        });

        findViewById(R.id.btnIdiomaCatalà).setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().playButtonSound();
            cambiarIdioma("ca");
        });

        findViewById(R.id.btnIdiomaEnglish).setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().playButtonSound();
            cambiarIdioma("en");
        });

        findViewById(R.id.btnIdiomaGerman).setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().playButtonSound();
            cambiarIdioma("de");
        });

        // ---- BOTÓN VOLVER AL MENÚ ----
        Button btnMenu = findViewById(R.id.Menu);
        btnMenu.setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().playButtonSound();
            startActivity(new Intent(Options.this, SplashScreen.class));
            finish();
        });

        // ---- BOTÓN ELEGIR MÚSICA ----
        findViewById(R.id.btnElegirMúsica).setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().playButtonSound();

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("audio/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 200);
        });
        // ---- BOTÓN ELEGIR MÚSICA ----
        findViewById(R.id.btnMusicaOriginal).setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().playButtonSound();
            BackgroundAudioManager.getInstance().playOriginalMusic(); // Música original

            // Opcional: borrar la música guardada en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("ajustes", MODE_PRIVATE);
            prefs.edit().remove("musica_personalizada").apply();

            Toast.makeText(this, "Volviendo a la música original", Toast.LENGTH_SHORT).show();
        });
    }

    // ---- CAMBIAR IDIOMA ----
    private void cambiarIdioma(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(
                config,
                getBaseContext().getResources().getDisplayMetrics()
        );

        finish();
        startActivity(getIntent());
    }

    // ---- RESULTADO DE SELECCIONAR MÚSICA ----
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {

            Uri selectedAudio = data.getData();

            getContentResolver().takePersistableUriPermission(
                    selectedAudio,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            // Aplicar música personalizada
            BackgroundAudioManager.getInstance().setCustomBackgroundMusic(selectedAudio);

            // Guardar en preferencias
            guardarMusicaPersonalizada(selectedAudio.toString());
        }
    }

    private void guardarMusicaPersonalizada(String uri) {
        SharedPreferences prefs = getSharedPreferences("ajustes", MODE_PRIVATE);
        prefs.edit().putString("musica_personalizada", uri).apply();
    }

    private String cargarMusicaPersonalizada() {
        SharedPreferences prefs = getSharedPreferences("ajustes", MODE_PRIVATE);
        return prefs.getString("musica_personalizada", null);
    }
}
