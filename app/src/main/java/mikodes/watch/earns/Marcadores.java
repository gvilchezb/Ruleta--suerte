package mikodes.watch.earns;

import android.content.Intent;
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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Marcadores extends AppCompatActivity {
    private ScoreDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_marcadores);

        dbHelper = new ScoreDatabaseHelper(this);

        // Cargar marcadores
        Single<List<String>> obtenerTop5 = Single.fromCallable(() -> {
            List<String> lista = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT fecha, puntuacion FROM marcadores ORDER BY puntuacion DESC LIMIT 5", null);
            while (cursor.moveToNext()) {
                String fecha = cursor.getString(0);
                int puntuacion = cursor.getInt(1);
                lista.add(fecha + " - " + puntuacion + " puntos");
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
            BackgroundAudioManager.getInstance().playSound(R.raw.button);
            // Abrir la pantalla de inicio
            Intent intent = new Intent(Marcadores.this, SplashScreen.class);
            startActivity(intent);
            finish();
        });
    }
}

