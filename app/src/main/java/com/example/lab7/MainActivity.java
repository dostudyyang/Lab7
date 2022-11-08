package com.example.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

import ca.roumani.i2c.MPro;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {

    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tts = new TextToSpeech(this,this);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }



    public void buttonClicked(View v){
        try{
            EditText cashpriceView = (EditText)findViewById(R.id.cashpriceBox);
            String cV = cashpriceView.getText().toString();
            EditText amortView = (EditText)findViewById(R.id.amortBox);
            String aV = amortView.getText().toString();
            EditText interestView = (EditText)findViewById(R.id.interestBox);
            String iV = interestView.getText().toString();

            MPro mp = new MPro();
            mp.setAmortization(aV);
            mp.setInterest(iV);
            mp.setPrinciple(cV);
            String s = "Monthly Payment =" + mp.computePayment("%,.2f");
            tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
            s += "\n\n";
            s += "By making this payments monthly for" + mp.getAmortization() + "years, the mortgage will be paid in full. But if you terminate the mortgage on its nth anniversary," +
                    " the balance still owing depends on n as shown below:";
            s += "\n\n\n";
            s += String.format("%8s", "n") + String.format("%16s", "Balance");
            s += "\n\n";
            int count = 0;
            for (int i = 0; i < 5; i++) {
                s += String.format("%8d", count) + mp.outstandingAfter(count, "%,16.0f");
                count++;
                s += "\n\n";
            }
            for (int i = 0; i < Integer.valueOf(mp.getAmortization()).intValue(); i += 5) {
                s += String.format("%8d", count) + mp.outstandingAfter(count, "%,16.0f");
                count += 5;
                s += "\n\n";
            }
            ((TextView)findViewById(R.id.Output)).setText(s);


        }catch (Exception e){
            Toast label = Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
            label.show();
        }
    }


    @Override
    public void onInit(int initStatus){
            this.tts.setLanguage(Locale.US);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(ax*ax + ay*ay + az*az);
        if (a > 10) {
            ((EditText) findViewById(R.id.cashpriceBox)).setText("");
            ((EditText) findViewById(R.id.amortBox)).setText("");
            ((EditText) findViewById(R.id.interestBox)).setText("");
            ((TextView) findViewById(R.id.Output)).setText("");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }
}
