package mikodes.watch.earns;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.TimeZone;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
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

    private void showVictoryNotification() {
        String channelId = "victory_channel";
        String channelName = "Notificaciones de Victoria";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear canal para Android 8+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }


        // Texto dinámico con la puntuación
        String contentText = "¡Has terminado la partida con " + coin + " puntos!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_victory) // Usa un ícono en res/drawable
                .setContentTitle("¡Victoria!")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }



    private void addVictoryToCalendar(long calendarId, String title, String description, long startMillis, long endMillis) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().getID());

        Uri inserted = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
        Log.d("Calendar", "Event inserted URI: " + inserted);

        // Opcional: añadir recordatorio (alarma de notificación)
        if (inserted != null) {
            long eventId = Long.parseLong(inserted.getLastPathSegment());
            ContentValues reminderValues = new ContentValues();
            reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
            reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            reminderValues.put(CalendarContract.Reminders.MINUTES, 0); // notificar al inicio
            Uri reminderUri = getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
            Log.d("Calendar", "Reminder inserted URI: " + reminderUri);
        }
    }




    private Long getDefaultVisibleCalendarId() {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.VISIBLE
        };

        String selection = CalendarContract.Calendars.VISIBLE + " = ?";
        String[] selectionArgs = new String[] { "1" };

        try (Cursor cur = getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cur != null && cur.moveToFirst()) {
                long id = cur.getLong(0);
                String name = cur.getString(2);
                Log.d("Calendar", "Calendario encontrado: " + name + " (ID: " + id + ")");
                return id;
            }
        } catch (Exception e) {
            Log.e("Calendar", "Error consultando calendarios", e);
        }
        return null; // No hay calendarios visibles
    }


    private static final int REQ_CALENDAR_PERMS = 202;

    private void ensureCalendarPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needed = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                needed.add(Manifest.permission.READ_CALENDAR);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                needed.add(Manifest.permission.WRITE_CALENDAR);
            }
            if (!needed.isEmpty()) {
                requestPermissions(needed.toArray(new String[0]), REQ_CALENDAR_PERMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CALENDAR_PERMS) {
            boolean granted = true;
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (!granted) {
                Toast.makeText(this, "Permisos de calendario denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private Long createLocalCalendar() {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, "LuckyWheel");
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(CalendarContract.Calendars.NAME, "LuckyWheel Calendar");
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "LuckyWheel Eventos");
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, "LuckyWheel");
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "LuckyWheel");
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);

        Uri uri = getContentResolver().insert(builder.build(), values);
        if (uri != null) {
            return Long.parseLong(uri.getLastPathSegment());
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_wheel);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, 102);
            }
        }

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
                BackgroundAudioManager.getInstance().playWheelSound();

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
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button btnGuardarSalir = findViewById(R.id.acabar);

        btnGuardarSalir.setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().playVictorySound();

            ensureCalendarPermissions(); // Pedir permisos si faltan

            // Verificar permisos de ubicación
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        double lat = 0.0;
                        double lon = 0.0;
                        String ciudad = "Desconocida";
                        String pais = "Desconocido";

                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();

                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                                if (!addresses.isEmpty()) {
                                    ciudad = addresses.get(0).getLocality();
                                    pais = addresses.get(0).getCountryName();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        double finalLat = lat;
                        double finalLon = lon;
                        String finalCiudad = ciudad;
                        String finalPais = pais;

                        Completable.fromAction(() -> {
                                    // 1) Guardar puntuación y ubicación en la base de datos
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put("fecha", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                                    values.put("puntuacion", coin);
                                    values.put("latitud", finalLat);
                                    values.put("longitud", finalLon);
                                    values.put("ciudad", finalCiudad);
                                    values.put("pais", finalPais);
                                    db.insert("marcadores", null, values);
                                    db.close();

                                    // 2) Intentar registrar en calendario
                                    Long calendarId = getDefaultVisibleCalendarId();
                                    if (calendarId != null) {
                                        long startMillis = System.currentTimeMillis();
                                        long endMillis = startMillis + (1 * 60 * 1000);
                                        addVictoryToCalendar(calendarId, "Victoria en LuckyWheel", "Has ganado con " + coin + " puntos", startMillis, endMillis);
                                    } else {
                                        Log.w("Calendar", "No hay calendario disponible, creando uno local...");
                                        calendarId = createLocalCalendar();
                                        if (calendarId != null) {
                                            long startMillis = System.currentTimeMillis();
                                            long endMillis = startMillis + 1000;
                                            addVictoryToCalendar(calendarId, "Victoria en LuckyWheel", "Has ganado con " + coin + " puntos", startMillis, endMillis);
                                        } else {
                                            Log.e("Calendar", "No se pudo crear un calendario local");
                                        }
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    Toast.makeText(getApplicationContext(), "Marcador guardado: " + finalCiudad + ", " + finalPais, Toast.LENGTH_SHORT).show();
                                    showVictoryNotification();
                                    startActivity(new Intent(LuckyWheel.this, Marcadores.class));
                            finish();
                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                        });

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
