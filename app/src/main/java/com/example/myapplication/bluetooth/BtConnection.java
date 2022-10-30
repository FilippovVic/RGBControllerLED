package com.example.myapplication.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.example.myapplication.adapter.BluetoothConstants;

public class BtConnection {
    private Context context;
    private SharedPreferences pref;
    private BluetoothAdapter btAdapter;
    private static BluetoothDevice device;
    private ConnectThread connectThread;
    private boolean state;

    public BtConnection(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(BluetoothConstants.MY_PREF, Context.MODE_PRIVATE);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized boolean connect() {
        String mac = pref.getString(BluetoothConstants.MAC_KEY, "");
        if (!btAdapter.isEnabled() || mac.isEmpty()) {
            Toast.makeText(context, "Включите блютуз!", Toast.LENGTH_SHORT).show();
            return false;
        }
        device = btAdapter.getRemoteDevice(mac);
        if (device == null) {
            Toast.makeText(context, "Не удалось подключиться!", Toast.LENGTH_SHORT).show();
            return false;
        }
        connectThread = new ConnectThread(device);
        connectThread.start();
        try {
            connectThread.join();
        } catch (Exception e) {
            e.getStackTrace();
        }
        state = connectThread.getBoolean();
        System.out.println(state);
        return state;
    }

    public boolean disconnect() {
        ConnectThread.closeConnection();
        state = false;
        return false;
    }

    public ConnectThread getConnectThread() {
        return connectThread;
    }

    public void sendMessage(byte[] bytes) {
        try {
            if (state) {
                connectThread.getRThread().sendMessage(bytes);
            }
        } catch (NullPointerException e) {
            e.getStackTrace();

        }
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public static String getName() {
        if (device != null) {
            return device.getName();
        } else {
            return "null";
        }
    }
}