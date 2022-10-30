package com.example.myapplication.bluetooth;

import android.bluetooth.BluetoothSocket;
import com.example.myapplication.adapter.BluetoothConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiveThread extends Thread{
    private InputStream inputS;
    private OutputStream outputS;

    public ReceiveThread(BluetoothSocket socket){
        try{
            inputS = socket.getInputStream();
        } catch (IOException ignored){

        }
        try{
            outputS = socket.getOutputStream();
        } catch (IOException ignored){

        }
    }

    @Override
    public void run() {
        byte[] rBuffer = new byte[2];
        while(true){
            try{
                int size = inputS.read(rBuffer);
                String message = new String(rBuffer, 0, size);
                try {
                    BluetoothConstants.answer = Integer.parseInt(message);
                } catch (NumberFormatException ignored) {

                }
            } catch (IOException e){
                break;
            }
        }
    }

    public void sendMessage(byte[] bytes){
        try{
            outputS.write(bytes);
        } catch (IOException e){
            e.getMessage();
        }
    }
}