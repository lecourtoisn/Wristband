package com.polytech.al.wristband;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.UUID;

public class AcceptConnexion extends Thread {
    private MainActivity activity;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket = null;
    private BluetoothAdapter adapter;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private Timer timer = new Timer();

    public AcceptConnexion(MainActivity activity) {
        this.activity = activity;
        adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            serverSocket = adapter.listenUsingRfcommWithServiceRecord("WristBand",
                    UUID.fromString("33d08e9e-ac0d-11e6-80f5-76304dec7eb7"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read() {
        try {
            byte[] bytes = new byte[1];
            while (true) {
                int read = inputStream.read(bytes);
                byte code = bytes[0];
                Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                if (code == 1) {
                    System.out.println("Droite");
                    v.vibrate(100);
                    Thread.sleep(300);
                    v.vibrate(100);
                    activity.setColor(Color.RED);
                } else if (code == 2) {
                    System.out.println("Gauche");
                    v.vibrate(200);
                    activity.setColor(Color.YELLOW);
                } else {
                    activity.setColor(Color.GREEN);
                }
                activity.handler.post(activity.changeColour);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            System.out.println("Seeking connexion...");

            try {
                socket = serverSocket.accept();
                if (socket != null) {
                    System.out.println("Connected !");

                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();
                    read();
                    System.out.println("Stopped reading");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
