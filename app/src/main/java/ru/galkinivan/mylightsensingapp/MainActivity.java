package ru.galkinivan.mylightsensingapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    //UI vars
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //UI asinings
        startButton = (Button) findViewById(R.id.buttonStartLightSensing);
        stopButton = (Button) findViewById(R.id.buttonStopLightSensing);

    }

    private void startSensing(View v){
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        sensorManager.registerListener(this, lightSensor, 1000000);
    }

    private void stopSensing(View v){
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSensing(v);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSensing(v);
            }
        });
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0];
        System.out.println("Light value: "+lightValue);
    }
}
