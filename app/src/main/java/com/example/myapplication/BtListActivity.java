package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.adapter.BtAdapter;
import com.example.myapplication.adapter.ListItem;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BtListActivity extends AppCompatActivity {
    private BtAdapter adapter;
    private BluetoothAdapter bluetoothAdapter;
    private List<ListItem> list;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_list);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toast.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toast.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(@NotNull Menu menu) {
        getMenuInflater().inflate(R.menu.devices_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            toast.cancel();
            finish();
            
        } else if (item.getItemId() == R.id.question) {
            if (toast != null) {
                toast.cancel();
            }
            assert toast != null;
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast = Toast.makeText(this, "Only paired devices are displayed in the list! In order for the Bluetooth module to appear in the list, pair it in the phone settings.", Toast.LENGTH_LONG);
            toast.show();
        }
        return true;
    }

    private void init(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList<>();
        ActionBar ab = getSupportActionBar();
        if(ab == null)return;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeActionContentDescription("Return");
        ab.setTitle("Devices");
        ListView listView = findViewById(R.id.listView);
        adapter = new BtAdapter(this, R.layout.bt_list_item, list);
        listView.setAdapter(adapter);
        getPairedDevices();
        toast = new Toast(this);
    }

    private void getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            list.clear();
            for (BluetoothDevice device : pairedDevices) {
                ListItem item = new ListItem();
                item.setBtDevice(device);
                list.add(item);
            }
            adapter.notifyDataSetChanged();
        }
    }
}