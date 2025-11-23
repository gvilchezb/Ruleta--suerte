package mikodes.watch.earns;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class LuckyWheel extends AppCompatActivity {

    List<LuckyItem> data = new ArrayList<>();
    private int coin = 200;  // Puntuación inicial
    private TextView tvCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_wheel);


        tvCoins = findViewById(R.id.tvCoins);
        tvCoins.setText("Monedas: " + coin);


        ImageView imageView = findViewById(R.id.imageView11);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final LuckyWheelView luckyWheelView = findViewById(R.id.luckyWheel);
        findViewById(R.id.play).setEnabled(true);
        findViewById(R.id.play).setAlpha(1f);


        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.text = "0";
        luckyItem1.color = Color.parseColor("#8574F1");
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = "-50";
        luckyItem2.color = Color.parseColor("#8E84FF");
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = "+50";
        luckyItem3.color = Color.parseColor("#752BEF");
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.text = "-100";
        luckyItem4.color = ContextCompat.getColor(getApplicationContext(), R.color.Spinwell140);
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.text = "+100";
        luckyItem5.color = Color.parseColor("#8574F1");
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.text = "-150";
        luckyItem6.color = Color.parseColor("#8E84FF");
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.text = "+150";
        luckyItem7.color = Color.parseColor("#752BEF");
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.text = "-200";
        luckyItem8.color = ContextCompat.getColor(getApplicationContext(), R.color.Spinwell140);
        data.add(luckyItem8);

        luckyWheelView.setData(data);
        luckyWheelView.setRound(getRandomRound());

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Reproducir sonido del botón
                BackgroundAudioManager.getInstance().playSound(R.raw.wheel);

                // Comprobar si hay monedas suficientes
                if (coin <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "No tienes monedas para jugar",
                            Toast.LENGTH_SHORT).show();
                    return; // No permite girar la ruleta
                }

                // Actualizar UI
                Toast.makeText(getApplicationContext(), (coin > 0 ? " " : "") + "-10" + " Monedas", Toast.LENGTH_SHORT).show();

                int index = getRandomIndex();
                    luckyWheelView.startLuckyWheelWithTargetIndex(index);

                    findViewById(R.id.play).setEnabled(false);
                    findViewById(R.id.play).setAlpha(.5f);
            }
        });
        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                int premio = 0; // premio base
                // Determinar premio según índice
                if (index == 1 ){
                     coin = coin + 0;
                    Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + "0" + " Monedas", Toast.LENGTH_SHORT).show();
                } if (index == 2 ){
                    coin = coin - 50;
                    Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + "-50" + " Monedas", Toast.LENGTH_SHORT).show();
                } if (index == 3 ){
                    coin = coin + 50;
                    Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + "+50" + " Monedas", Toast.LENGTH_SHORT).show();
                } if (index == 4 ){
                    coin = coin -100;
                    Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + "-100" + " Monedas", Toast.LENGTH_SHORT).show();
                } if (index == 5){
                    coin = coin + 100;
                    Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + "+100" + " Monedas", Toast.LENGTH_SHORT).show();
                } if (index == 6 ){
                    coin = coin - 150;
                    Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + "-150" + " Monedas", Toast.LENGTH_SHORT).show();
                } if (index == 7 ){
                    coin = coin + 150;
                    Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + "+150" + " Monedas", Toast.LENGTH_SHORT).show();
                } if (index == 8 ){
                    coin = coin - 200;
                    Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + "-200" + " Monedas", Toast.LENGTH_SHORT).show();
                }

                // restar 10 por jugar y sumar premio
                coin -= 10; // coste por jugar

                coin += premio;

                // Actualizar UI
                tvCoins.setText("Monedas: " + coin);
                Toast.makeText(getApplicationContext(), (premio >= 0 ? " " : "") + coin + " Monedas", Toast.LENGTH_SHORT).show();

                // Comprobar si se acabó el juego
                if (coin <= 0) {
                    Toast.makeText(getApplicationContext(),"¡Juego terminado! Te quedaste sin monedas.", Toast.LENGTH_LONG).show();

                    // Volver al SplashScreen
                    startActivity(new Intent(LuckyWheel.this, SplashScreen.class));
                    finish();
                }

                // Reactivar botón
                findViewById(R.id.play).setEnabled(true);
                findViewById(R.id.play).setAlpha(1f);


            }
        });
        ScoreDatabaseHelper dbHelper = new ScoreDatabaseHelper(this);

        Button btnGuardarSalir = findViewById(R.id.acabar);
        btnGuardarSalir.setOnClickListener(v -> {

            // Reproducir sonido del botón
            BackgroundAudioManager.getInstance().playSound(R.raw.victory);

            Completable.fromAction(() -> {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("fecha", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                        values.put("puntuacion", coin);
                        db.insert("marcadores", null, values);
                        db.close();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Toast.makeText(getApplicationContext(), "Puntuación guardada", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LuckyWheel.this, Marcadores.class));
                        finish();
                    }, throwable -> {
                        Toast.makeText(getApplicationContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                    });
        });

    }
    private int getRandomIndex() {
        int[] ind = new int[] {1,2,3,4,5,6,7,8};
        int rand = new Random().nextInt(ind.length);
        return ind[rand];
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }

    @Override
    public void onBackPressed() {
       Intent intent = new Intent(this,SplashScreen.class);
       startActivity(intent);
    }

}
