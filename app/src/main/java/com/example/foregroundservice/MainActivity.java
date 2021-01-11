package com.example.foregroundservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.foregroundservice.interfaces.BluetoothReceiverInterface;
import com.example.foregroundservice.interfaces.NetworkReceiverInterface;
import com.example.foregroundservice.receiver.BluetoothReceiver;
import com.example.foregroundservice.receiver.NetworkReceiver;
import com.example.foregroundservice.service.NotificationService;
import com.example.foregroundservice.util.NetworkUtil;

public class MainActivity extends AppCompatActivity implements BluetoothReceiverInterface, NetworkReceiverInterface {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BluetoothAdapter bluetoothAdapter;
    private TextView networkStatus, bluetoothStatus;
    private Switch bluetoothSwitch;
    private BluetoothReceiver mBluetoothReceiver;
    private NetworkReceiver mNetworkReceiver;
    private ImageView bluetoothImage, networkImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBluetoothReceiver = new BluetoothReceiver(this);
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        mNetworkReceiver = new NetworkReceiver(this);
        registerReceiver(mNetworkReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);
        networkStatus = findViewById(R.id.wifiStatus);
        bluetoothStatus = findViewById(R.id.bluetoothStatus);
        networkImage = findViewById(R.id.imageView);
        bluetoothImage = findViewById(R.id.imageView2);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled()) {
            isBluetoothOn();
        } else {
            isBluetoothOff();
        }

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    turnOn();
                } else {
                    turnOff();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);
        unregisterReceiver(mNetworkReceiver);
    }

    public void turnOn() {
        if (!bluetoothAdapter.isEnabled()) {
//            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(turnOn, 0);
            bluetoothAdapter.enable();
        }
    }

    public void turnOff() {
        bluetoothAdapter.disable();
    }

    @Override
    public void handleBluetoothState(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                isBluetoothOff();
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                bluetoothStatus.setText(R.string.status_turning_off);
                break;
            case BluetoothAdapter.STATE_ON:
                isBluetoothOn();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                bluetoothStatus.setText(R.string.status_turning_on);
                break;
        }
    }

    @Override
    public void handleNetworkState(int state) {
        switch (state) {
            case NetworkUtil.TYPE_WIFI:
                isNetworkWifiOn();
                break;
            case NetworkUtil.TYPE_MOBILE:
                isNetworkMobileOn();
                break;
            case NetworkUtil.TYPE_NOT_CONNECTED:
                isNetworkOff();
                break;
        }
    }

    public void startNotificationService(View v) {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        int bluetoothStatus = R.string.status_off;
        if (bluetoothAdapter.isEnabled()) {
            bluetoothStatus = R.string.status_on;
        } else {
            bluetoothStatus = R.string.status_off;
        }
        serviceIntent.putExtra("bluetoothStatus", bluetoothStatus);
        startService(serviceIntent);
    }

    public void stopNotificationService(View v) {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        stopService(serviceIntent);
    }

    private void isBluetoothOn() {
        bluetoothImage.setColorFilter(ContextCompat.getColor(this, R.color.teal_200), android.graphics.PorterDuff.Mode.SRC_IN);
        bluetoothSwitch.setChecked(true);
        bluetoothStatus.setText(R.string.status_on);
    }

    private void isBluetoothOff() {
        bluetoothImage.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        bluetoothSwitch.setChecked(false);
        bluetoothStatus.setText(R.string.status_off);
    }

    private void isNetworkWifiOn() {
        networkImage.setImageResource(R.drawable.wifi_icon);
        networkImage.setColorFilter(ContextCompat.getColor(this, R.color.teal_200), android.graphics.PorterDuff.Mode.SRC_IN);
        networkStatus.setText(R.string.wifi_on);
    }

    private void isNetworkOff() {
        networkImage.setImageResource(R.drawable.ic_no_connection);
        networkImage.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        networkStatus.setText(R.string.status_off);
    }

    private void isNetworkMobileOn() {
        networkImage.setImageResource(R.drawable.ic_mobile_data);
        networkImage.setColorFilter(ContextCompat.getColor(this, R.color.teal_200), android.graphics.PorterDuff.Mode.SRC_IN);
        networkStatus.setText(R.string.mobile_data_on);
    }

}