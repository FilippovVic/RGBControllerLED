package com.example.myapplication.adapter;

import android.bluetooth.BluetoothDevice;

public class ListItem {

    private BluetoothDevice btDevice;

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }
}
