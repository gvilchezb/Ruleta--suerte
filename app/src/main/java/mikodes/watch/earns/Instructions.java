package mikodes.watch.earns;

import android.content.Intent;
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
import java.util.Random;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;



public class Instructions extends AppCompatActivity{

    @Override
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
