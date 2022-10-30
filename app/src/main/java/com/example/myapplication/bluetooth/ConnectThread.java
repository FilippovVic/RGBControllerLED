package com.example.myapplication.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;

public class ConnectThread extends Thread {
    private static BluetoothSocket bluetoothSocket;
    private ReceiveThread receiveThread;
    private boolean state;
    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";

    public ConnectThread(BluetoothDevice device){
        try{
            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
        } catch (IOException ignored){

        }
    }

    @Override
    public synchronized void run() {
        try{
            bluetoothSocket.connect();
            receiveThread = new ReceiveThread(bluetoothSocket);
            receiveThread.start();
            state = true;
        } catch (IOException e){
            closeConnection();
            state = false;
        }
    }

    public boolean getBoolean() {
        return state;
    }

    public static void closeConnection(){
        try{
            bluetoothSocket.close();
        } catch (IOException ignored){

        }
    }

    public BluetoothSocket getSocket() {
        return bluetoothSocket;
    }

    public ReceiveThread getRThread() {
        return receiveThread;
    }
}