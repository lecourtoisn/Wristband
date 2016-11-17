package com.polytech.al.wristband;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.os.Vibrator;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 42;
    private static final UUID uuid = UUID.fromString("33d08e9e-ac0d-11e6-80f5-76304dec7eb7");
    private BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice phone = null;
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals("78:00:9E:18:71:5D")) {
                    phone = device;
                }
            }
        }
        assert(phone != null);
        try {
            socket = phone.createRfcommSocketToServiceRecord(uuid);
            System.out.println("Trying to connect");
            socket.connect();
            Toast.makeText(MainActivity.this, "Connected to " + socket.getRemoteDevice().getName(), Toast.LENGTH_SHORT).show();

            InputStream inputStream = socket.getInputStream();

            byte[] bytes = new byte[1];
            while (true) {
                inputStream.read(bytes);
                byte code = bytes[0];

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                View view = findViewById(R.id.screen);
//                View root = view.getRootView();

                int duration = (code == 0 ? 100 : 200);
                int bgColor = (code == 0 ? Color.GREEN : Color.RED);
                System.out.println(bgColor);

                v.vibrate(duration);
//                root.setBackgroundColor(bgColor);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }


}
