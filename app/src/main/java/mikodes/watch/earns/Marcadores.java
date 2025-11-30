package mikodes.watch.earns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Marcadores extends AppCompatActivity {
    private ScoreDatabaseHelper dbHelper;
    public static final String COL_CITY = "ciudad";
    public static final String COL_COUNTRY = "pais";



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
        setContentView(R.layout.acitivity_marcadores);

        dbHelper = new ScoreDatabaseHelper(this);

        // Cargar marcadores
        Single<List<String>> obtenerTop5 = Single.fromCallable(() -> {
            List<String> lista = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT fecha, puntuacion, ciudad, pais FROM marcadores ORDER BY puntuacion DESC LIMIT 5", null);
            while (cursor.moveToNext()) {
                String fecha = cursor.getString(0);
                int puntuacion = cursor.getInt(1);
                String ciudad = cursor.getString(2);
                String pais = cursor.getString(3);
                lista.add(fecha + " - " + puntuacion + " puntos - " + ciudad + ", " + pais);
            }

            cursor.close();
            db.close();
            return lista;
        });

        obtenerTop5.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lista -> {
                    TextView tvMarcadores = findViewById(R.id.tvMarcadores);
                    tvMarcadores.setText(TextUtils.join("\n", lista));
                }, throwable -> {
                    Toast.makeText(this, "Error al cargar marcadores", Toast.LENGTH_SHORT).show();
                });

        // Botón Menú
        Button btnMenu = findViewById(R.id.Menu);
        btnMenu.setOnClickListener(v -> {
            // Reproducir sonido del botón
            BackgroundAudioManager.getInstance().playButtonSound();
            // Abrir la pantalla de inicio
            Intent intent = new Intent(Marcadores.this, SplashScreen.class);
            startActivity(intent);
            finish();
        });
    }
}

