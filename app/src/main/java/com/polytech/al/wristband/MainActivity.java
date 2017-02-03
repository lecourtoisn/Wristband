package com.polytech.al.wristband;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

//    private static final int REQUEST_ENABLE_BT = 42;
    private RelativeLayout RL;
    private static final UUID uuid = UUID.fromString("33d08e9e-ac0d-11e6-80f5-76304dec7eb7");

    private BluetoothSocket socket;
    private int color;

    public Handler handler = new Handler();

     public Runnable changeColour = new Runnable() {
        public void run() {
            RL.setBackgroundColor(color);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RL = (RelativeLayout)findViewById(R.id.Layout);

        new AcceptConnexion(this).start();

    }


    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
