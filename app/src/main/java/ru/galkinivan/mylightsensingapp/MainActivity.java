package ru.galkinivan.mylightsensingapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Integer friquancy;
    private double timeIntervalDouble;
    private Integer timeInterval;
    private Integer solvedPowerOfTwo;
    private Integer numberOfValuesRegistred;
    //UI vars
    private Button startButton;
    private Button stopButton;
    private TextView friquancyTextView;
    private TextView powerOfTwoTextView;
    private TextView resultTextView;


    static {
        //System.loadLibrary("main");
    }
    native void main();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //UI assigning
        startButton = (Button) findViewById(R.id.buttonStartLightSensing);
        stopButton = (Button) findViewById(R.id.buttonStopLightSensing);
        friquancyTextView = (TextView) findViewById(R.id.editTextFrequency);
        powerOfTwoTextView = (TextView) findViewById(R.id.editTextPowerOfTwo);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

    }

    private void startSensing(View v){
        boolean frCheck = false;
        boolean potCheck = false;
        // get and check values
        if(!"".equals(friquancyTextView.getText().toString())) {
            friquancy = Integer.parseInt(friquancyTextView.getText().toString());
            if(friquancy < 1000000 && friquancy > 0) {
                frCheck = true;
                timeIntervalDouble = 1000000.0 / friquancy;
                timeInterval = Double.valueOf(Math.ceil(timeIntervalDouble)).intValue(); // losing some resolution...
                System.out.println("Set frrquancy is: " + friquancy + " and time interval is: " + timeInterval);
            }
            else
                friquancyTextView.setError(getResources().getString(R.string.badValueError));
        }
        else
            friquancyTextView.setError(getResources().getString(R.string.requaredFeildError));

        if(!"".equals(powerOfTwoTextView.getText().toString())) {
            potCheck = true;
            solvedPowerOfTwo = Double.valueOf(Math.pow(2, Double.parseDouble(powerOfTwoTextView.getText().toString()))).intValue();
        }
        else
            powerOfTwoTextView.setError(getResources().getString(R.string.requaredFeildError));

        // run routine if all good
        if (frCheck && potCheck) {
            startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);
            numberOfValuesRegistred = 0;
            resultTextView.setText("");
            resultTextView.setMaxLines(solvedPowerOfTwo);
            resultTextView.setMovementMethod(new ScrollingMovementMethod());
            System.out.println("Starting light sensor with fr "+friquancy+" and collecting "+solvedPowerOfTwo+" values...");
            sensorManager.registerListener(this, lightSensor, timeInterval);
        }
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
        System.out.println("Sensor: "+sensor.getName()+" has changed its accuracy to "+accuracy);
        resultTextView.setText(resultTextView.getText()+"Смена точности на "+accuracy+" для "+sensor.getName()+"\n");
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0];
        //System.out.println("Light value: "+lightValue);
        if(numberOfValuesRegistred <= solvedPowerOfTwo){
            String addintionStr = Float.valueOf(event.values[0]).toString()+"\n";
            resultTextView.setText(resultTextView.getText()+addintionStr);
            ++numberOfValuesRegistred;
        }else{
            stopSensing(null);
        }
    }
}
