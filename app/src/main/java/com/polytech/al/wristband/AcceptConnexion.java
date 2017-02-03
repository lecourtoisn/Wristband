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

    private boolean mustVibrate = false;
    private boolean aroundBeacon = false;

    public void read() {
        try {
            byte[] bytes = new byte[1];
            while (true) {
                int read = inputStream.read(bytes);
                byte code = bytes[0];
                Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                System.out.println("recu "+code);
                if (!aroundBeacon) {
                    if (!mustVibrate && code == 1) {
                        // droite si feu au vert
                        System.out.println("Droite");
                        v.vibrate(100);
                        Thread.sleep(300);
                        v.vibrate(100);
                        activity.setColor(Color.RED);
                    } else if (!mustVibrate && code == 2) {
                        // gauche si feu au vert
                        System.out.println("Gauche");
                        v.vibrate(200);
                        activity.setColor(Color.YELLOW);
                    } else if (code == 3) {
                        // feu au rouge
                        mustVibrate = true;
                        System.out.println("Vibration en continue");
                        activity.setColor(Color.MAGENTA);
                    } else if (code == 4) {
                        // fin de feu au rouge, donc vert vraisemblablement
                        mustVibrate = false;
                    } else if (code == 5) {
                        aroundBeacon = true;
                        // si j'ai approché un beacon
                        activity.setColor(Color.LTGRAY);
                    } else if (mustVibrate) {
                        activity.setColor(Color.MAGENTA);
                    } else {
                        activity.setColor(Color.GREEN);
                    }
                } else {
                    if (code == 5) {
                        System.out.println("recu 5");
                        aroundBeacon = true;
                        // si j'ai approché un beacon
                        activity.setColor(Color.LTGRAY);
                    } else if (code == 6) {
                        System.out.println("recu 6");
                        // si je suis dans la bonne direction après avoir approché un beacon
                        activity.setColor(Color.DKGRAY);
                    }
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
