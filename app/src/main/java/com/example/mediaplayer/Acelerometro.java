package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Acelerometro extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView accelerationTextView;

    private Button startButton;
    private Button stopButton;
    private boolean isStarted;
    private MediaPlayer mediaPlayer;
    private MediaPlayer victorySound;
    private MediaPlayer defeatSound;
    private int mov = 0;
    private int nomove = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);

        mediaPlayer = MediaPlayer.create(this, R.raw.fondo);
        mediaPlayer.start();

        victorySound = MediaPlayer.create(this, R.raw.victory);
        defeatSound = MediaPlayer.create(this, R.raw.defeat);

        accelerationTextView = findViewById(R.id.accelerationTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Agregar un OnClickListener para el botón "Start"
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Registrar el SensorEventListener para el acelerómetro
                sensorManager.registerListener(Acelerometro.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                startButton.setEnabled(false); // Deshabilitar el botón "Start"
                stopButton.setEnabled(true); // Habilitar el botón "Stop"
                isStarted = true;
                nomove =1;
            }
        });

        // Agregar un OnClickListener para el botón "Stop"
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Detener la detección del acelerómetro
                sensorManager.unregisterListener(Acelerometro.this);
                startButton.setEnabled(true); // Habilitar el botón "Start"
                stopButton.setEnabled(false); // Deshabilitar el botón "Stop"
                isStarted = false;

                // Comprobar si se ha reproducido el sonido de victoria
                if (nomove == 1) {
                    // No se ha reproducido el sonido de victoria, reproducir el sonido de derrota
                    mediaPlayer.pause();
                    defeatSound.start();

                    defeatSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // Reanudar la música de fondo
                            mediaPlayer.start();
                        }
                    });
                }
            }
        });

        // Deshabilitar el botón "Stop" al inicio
        stopButton.setEnabled(false);

        // Mostrar la información del sensor de aceleración en el TextView
        Resources res = getResources();
        String acceleration = res.getString(R.string.acceleration);
        String initialText = acceleration + " (x, y, z): (0.00, 0.00, 0.00)";
        accelerationTextView.setText(initialText);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detener la detección del acelerómetro cuando la actividad se pausa
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Actualizar el TextView con los valores de aceleración
            Resources res = getResources();
            String acceleration = res.getString(R.string.acceleration);
            String text = acceleration + " (x, y, z): (" + x + ", " + y + ", " + z + ")";
            accelerationTextView.setText(text);

            // Comprobar si se cumple la condición para reproducir el sonido de victoria
            if (event.values[0] < -10 && mov == 0) {
                mov++;
                nomove = 1;

            } else {
                if (event.values[0] > 10 && mov == 1) {
                    mov++;
                }
            }
            if (mov == 2) {
                mov = 0;
                nomove = 0;
                mediaPlayer.pause();
                victorySound.start();

                victorySound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // Reanudar la música de fondo
                        mediaPlayer.start();
                    }
                });
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario hacer nada aquí
    }
}