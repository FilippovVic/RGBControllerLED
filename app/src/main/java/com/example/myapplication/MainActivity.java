package com.example.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import com.example.myapplication.adapter.BluetoothConstants;
import com.example.myapplication.bluetooth.BtConnection;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private MenuItem bluetooth_button;
    static public MenuItem connection_button;
    private MenuItem devices_menu;
    private BluetoothAdapter bluetoothAdapter;
    private SharedPreferences preferences;
    private BtConnection btConnection;
    private static boolean connection = false;
    private ImageView diagramView;
    private Bitmap bitmap;
    private int pixel;
    private int red;
    private int green;
    private int blue;
    private final byte first_command = 0x01;
    private byte second_command = 0x00;
    private final byte third_command = 0x00;
    private byte[] bytes;
    private static int counter = 0;
    private Toast toast;
    private SharedPreferences sharedPreferences;
    private SeekBar seekBar;
    private TextView brightnessText;
    private ImageView saloonIcon;
    private ImageView steeringWheelIcon;
    private ImageView leftDoorIcon;
    private ImageView leftBackDoorIcon;
    private ImageView rightDoorIcon;
    private ImageView rightBackDoorIcon;
    private ImageView climateIcon;
    private ImageView leftSeatIcon;
    private ImageView rightSeatIcon;
    private ImageView leftBackSeatIcon;
    private ImageView rightBackSeatIcon;
    private CheckBox climateBox;
    private CheckBox leftSeatBox;
    private CheckBox rightSeatBox;
    private CheckBox leftBackSeatBox;
    private CheckBox rightBackSeatBox;
    private CheckBox saloonBox;
    private CheckBox steeringWheelBox;
    private CheckBox leftDoorBox;
    private CheckBox rightDoorBox;
    private CheckBox leftBackDoorBox;
    private CheckBox rightBackDoorBox;
    private List<CheckBox> checks = new ArrayList<>();
    private List<ImageView> icons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void setSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bytes = new byte[]{first_command, second_command, third_command, ((byte) ((red * seekBar.getProgress()) / 100)), ((byte) ((green * seekBar.getProgress()) / 100)), ((byte) ((blue * seekBar.getProgress()) / 100))};
                if (BluetoothConstants.answer == 9 && connection) {
                    BluetoothConstants.answer = 0;
                    btConnection.sendMessage(bytes);
                    while (BluetoothConstants.answer != 9) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                seekBar.getThumb().setTint(Color.rgb(red, green, blue));

                brightnessText.setText("Brightness: " + seekBar.getProgress() + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bytes = new byte[]{first_command, second_command, third_command, ((byte) ((red * seekBar.getProgress()) / 100)), ((byte) ((green * seekBar.getProgress()) / 100)), ((byte) ((blue * seekBar.getProgress()) / 100))};
                if (BluetoothConstants.answer == 9 && connection) {
                    BluetoothConstants.answer = 0;
                    btConnection.sendMessage(bytes);
                    while (BluetoothConstants.answer != 9) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }


                seekBar.getThumb().setTint(Color.rgb(red, green, blue));

                brightnessText.setText("Brightness: " + seekBar.getProgress() + "%");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("value", seekBar.getProgress());
                editor.apply();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toast.cancel();
        connection_button.setIcon(R.drawable.ic_not_connected);
        counter = 0;
        if (connection) {
            connection = btConnection.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        toast.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(@NotNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        bluetooth_button = menu.findItem(R.id.id_bluetooth_button);
        connection_button = menu.findItem(R.id.id_connect);
        if (connection && connection_button != null) {
            connection_button.setIcon(R.drawable.ic_connected);
        }
        devices_menu = menu.findItem(R.id.id_menu);
        setBluetoothIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.id_bluetooth_button) {
            if (!bluetoothAdapter.isEnabled()) {
                enableBluetooth();
                counter = 0;
            } else {
                bluetoothAdapter.disable();
            }
        } else if (item.getItemId() == R.id.id_menu) {
            if (bluetoothAdapter.isEnabled()) {
                Intent i = new Intent(MainActivity.this, BtListActivity.class);
                startActivity(i);
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(this, "Enable Bluetooth!", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (item.getItemId() == R.id.id_connect) {
            if (bluetoothAdapter.isEnabled() && Objects.equals(preferences.getString(BluetoothConstants.MAC_KEY, "mac_key"), "mac_key")) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(this, "Choose device!", Toast.LENGTH_SHORT);
                toast.show();
                connection = false;
                setConnectionItem();
            } else if (!bluetoothAdapter.isEnabled()) {
                connection = false;
                setConnectionItem();
            }
            if (bluetoothAdapter.isEnabled() && !Objects.equals(preferences.getString(BluetoothConstants.MAC_KEY, "mac_key"), "mac_key") && counter == 0) {
                connection_button.setEnabled(false);
                connection = btConnection.connect();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (connection) {
                    counter = 1;
                }
                setConnectionItem();
                connection_button.setEnabled(true);
            } else if (bluetoothAdapter.isEnabled() && !Objects.equals(preferences.getString(BluetoothConstants.MAC_KEY, "mac_key"), "mac_key") && counter == 1) {
                connection_button.setEnabled(false);
                connection = btConnection.disconnect();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setConnectionItem();
                counter = 0;
                connection_button.setEnabled(true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setConnectionItem() {
        if (bluetoothAdapter.isEnabled() && connection) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(this, "Connection established!", Toast.LENGTH_SHORT);
            toast.show();
            connection_button.setIcon(R.drawable.ic_connected);
        } else if (bluetoothAdapter.isEnabled() && !connection && !Objects.equals(preferences.getString(BluetoothConstants.MAC_KEY, "mac_key"), "mac_key")) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(this, "Connection not established!", Toast.LENGTH_SHORT);
            toast.show();
            connection_button.setIcon(R.drawable.ic_not_connected);
        } else if (!bluetoothAdapter.isEnabled()) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(this, "Enable Bluetooth and connect your device!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void enableBluetooth() {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    private void setBluetoothIcon() {
        if (bluetoothAdapter.isEnabled()) {
            bluetooth_button.setIcon(R.drawable.ic_bt_enable);
            devices_menu.setIcon(R.drawable.ic_menu);
        } else {
            bluetooth_button.setIcon(R.drawable.ic_bt_disable);
            devices_menu.setIcon(R.drawable.ic_menu_unreachable);
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        preferences = getSharedPreferences(BluetoothConstants.MY_PREF, Context.MODE_PRIVATE);
        btConnection = new BtConnection(this);
        diagramView = findViewById(R.id.diagram);
        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter intentFilter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(receiver, intentFilter1);
        registerReceiver(receiver, intentFilter2);
        saloonIcon = findViewById(R.id.saloonIcon);
        leftDoorIcon = findViewById(R.id.leftDoorIcon);
        leftBackDoorIcon = findViewById(R.id.leftBackDoorIcon);
        rightDoorIcon = findViewById(R.id.rightDoorIcon);
        rightBackDoorIcon = findViewById(R.id.rightBackDoorIcon);
        steeringWheelIcon = findViewById(R.id.steeringWheelIcon);
        saloonBox = findViewById(R.id.saloonBox);
        leftDoorBox = findViewById(R.id.leftDoorBox);
        rightDoorBox = findViewById(R.id.rightDoorBox);
        leftBackDoorBox = findViewById(R.id.leftBackDoorBox);
        rightBackDoorBox = findViewById(R.id.rightBackDoorBox);
        steeringWheelBox = findViewById(R.id.steeringWheelBox);
        climateIcon = findViewById(R.id.climateIcon);
        leftSeatIcon = findViewById(R.id.seat1);
        rightSeatIcon = findViewById(R.id.seat2);
        leftBackSeatIcon = findViewById(R.id.seat3);
        rightBackSeatIcon = findViewById(R.id.seat4);
        climateBox = findViewById(R.id.climateBox);
        leftSeatBox = findViewById(R.id.leftSeatBox);
        rightSeatBox = findViewById(R.id.rightSeatBox);
        leftBackSeatBox = findViewById(R.id.backLeftSeatBox);
        rightBackSeatBox = findViewById(R.id.backRightSeatBox);
        checks.add(saloonBox);
        checks.add(leftDoorBox);
        checks.add(rightDoorBox);
        checks.add(leftBackDoorBox);
        checks.add(rightBackDoorBox);
        checks.add(steeringWheelBox);
        checks.add(climateBox);
        checks.add(leftSeatBox);
        checks.add(rightSeatBox);
        checks.add(leftBackSeatBox);
        checks.add(rightBackSeatBox);
        icons.add(saloonIcon);
        icons.add(leftDoorIcon);
        icons.add(leftBackDoorIcon);
        icons.add(rightDoorIcon);
        icons.add(rightBackDoorIcon);
        icons.add(steeringWheelIcon);
        icons.add(climateIcon);
        icons.add(leftSeatIcon);
        icons.add(rightSeatIcon);
        icons.add(leftBackSeatIcon);
        icons.add(rightBackSeatIcon);
        toast = new Toast(this);
        seekBar = findViewById(R.id.seekBar);
        brightnessText = findViewById(R.id.brightness);
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        red = sharedPreferences.getInt("red", 252);
        green = sharedPreferences.getInt("green", 207);
        blue = sharedPreferences.getInt("blue", 52);
        seekBar.getThumb().setTint(Color.rgb(red, green, blue));
        seekBar.getProgressDrawable().setTint(Color.rgb(red, green, blue));
        seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(red/2, green/2, blue/2)));
        seekBar.setProgress(sharedPreferences.getInt("value", 50));
        saloonIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        leftDoorIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        leftBackDoorIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        rightDoorIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        rightBackDoorIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        steeringWheelIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        climateIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        leftSeatIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        rightSeatIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        rightBackSeatIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        leftBackSeatIcon.getDrawable().setTint(Color.rgb(red, green, blue));
        brightnessText.setText("Brightness: " + seekBar.getProgress() + "%");
        setDiagram();
        setSeekBar();
        setCheckBoxes();
    }

    private void setCheckBoxes(){
        saloonBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == saloonBox.getId());
                    second_command = 0x00;
                }
            } else {
                compoundButton.setChecked(false);
            }
        });
        steeringWheelBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == steeringWheelBox.getId());
                    second_command = 0x01;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        leftDoorBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == leftDoorBox.getId());
                    second_command = 0x02;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        rightDoorBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == rightDoorBox.getId());
                    second_command = 0x03;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        rightBackDoorBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == rightBackDoorBox.getId());
                    second_command = 0x05;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        leftBackDoorBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == leftBackDoorBox.getId());
                    second_command = 0x04;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        climateBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == climateBox.getId());
                    second_command = 0x10;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        leftSeatBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == leftSeatBox.getId());
                    second_command = 0x06;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        rightSeatBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == rightSeatBox.getId());
                    second_command = 0x07;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        leftBackSeatBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == leftBackSeatBox.getId());
                    second_command = 0x08;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
        rightBackSeatBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (CheckBox box : checks) {
                    box.setChecked(box.getId() == rightBackSeatBox.getId());
                    second_command = 0x09;
                }
            } else {
                compoundButton.setChecked(false);
                second_command = 0x00;
            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setDiagram() {
        diagramView.setDrawingCacheEnabled(true);
        diagramView.buildDrawingCache(true);
        diagramView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                bitmap = diagramView.getDrawingCache();
                try {
                    pixel = bitmap.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());
                } catch (IllegalArgumentException ignored) {

                }
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                bytes = new byte[]{first_command, second_command, third_command, ((byte) ((red * seekBar.getProgress()) / 100)), ((byte) ((green * seekBar.getProgress()) / 100)), ((byte) ((blue * seekBar.getProgress()) / 100))};

                if (BluetoothConstants.answer == 9) {
                    if (connection) {
                        BluetoothConstants.answer = 0;
                        btConnection.sendMessage(bytes);
                        while (BluetoothConstants.answer != 9) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (second_command == 0) {
                    for (ImageView icon : icons) {
                        icon.getDrawable().setTint(Color.rgb(red, green, blue));
                    }
                } else if (second_command == 1) {
                    steeringWheelIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 2) {
                    leftDoorIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 3) {
                    rightDoorIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 4) {
                    leftBackDoorIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 5) {
                    rightBackDoorIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 0x10) {
                    climateIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 6) {
                    leftSeatIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 7) {
                    rightSeatIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 8) {
                    leftBackSeatIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                } else if (second_command == 9) {
                    rightBackSeatIcon.getDrawable().setTint(Color.rgb(red, green, blue));
                }
                seekBar.getThumb().setTint(Color.rgb(red, green, blue));
                seekBar.getProgressDrawable().setTint(Color.rgb(red, green, blue));


                seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(red/2, green/2, blue/2)));


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("red", Color.red(pixel));
                editor.putInt("green", Color.green(pixel));
                editor.putInt("blue", Color.blue(pixel));
                editor.apply();
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_UP && red == 0 && green == 0 && blue == 0) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(this, "Diodes are down!)", Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetooth_button.setIcon(R.drawable.ic_bt_enable);
                    devices_menu.setIcon(R.drawable.ic_menu);
                } else {
                    bluetooth_button.setIcon(R.drawable.ic_bt_disable);
                    devices_menu.setIcon(R.drawable.ic_menu_unreachable);
                    if (btConnection.getConnectThread() != null) {
                        connection = btConnection.disconnect();
                        connection_button.setIcon(R.drawable.ic_not_connected);
                    }
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    if (Objects.equals(bluetoothDevice.getAddress(), btConnection.getDevice().getAddress()) && connection && btConnection.getConnectThread().getSocket() != null) {
                        connection_button.setIcon(R.drawable.ic_not_connected);
                        counter = 0;
                        makeInterruptText();
                        connection = false;
                    }
                } catch (Exception ignored) {

                }
            }
        }
    };

    private void makeInterruptText() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Connection lost!", Toast.LENGTH_SHORT);
        toast.show();
    }
}