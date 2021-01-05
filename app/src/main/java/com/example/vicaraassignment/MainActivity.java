package com.example.vicaraassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vicaraassignment.interfaces.BluetoothReceiverInterface;
import com.example.vicaraassignment.interfaces.NetworkReceiverInterface;
import com.example.vicaraassignment.receiver.BluetoothReceiver;
import com.example.vicaraassignment.receiver.NetworkReceiver;
import com.example.vicaraassignment.util.NetworkUtil;

public class MainActivity extends AppCompatActivity implements BluetoothReceiverInterface, NetworkReceiverInterface {
    private BluetoothAdapter bluetoothAdapter;
    private TextView networkStatus, bluetoothStatus;
    private Switch bluetoothSwitch;
    private BluetoothReceiver mBluetoothReceiver;
    private NetworkReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothReceiver = new BluetoothReceiver(this);
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        mNetworkReceiver = new NetworkReceiver(this);
        registerReceiver(mNetworkReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);
        networkStatus = (TextView) findViewById(R.id.wifiStatus);
        bluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled()) {
            bluetoothSwitch.setChecked(true);
            bluetoothStatus.setText(R.string.status_on);
        } else {
            bluetoothSwitch.setChecked(false);
            bluetoothStatus.setText(R.string.status_off);
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
                bluetoothSwitch.setChecked(false);
                bluetoothStatus.setText(R.string.status_off);
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                bluetoothStatus.setText(R.string.status_turning_off);
                break;
            case BluetoothAdapter.STATE_ON:
                bluetoothSwitch.setChecked(true);
                bluetoothStatus.setText(R.string.status_on);
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
                networkStatus.setText(R.string.wifi_on);
                break;
            case NetworkUtil.TYPE_MOBILE:
                networkStatus.setText(R.string.mobile_data_on);
                break;
            case NetworkUtil.TYPE_NOT_CONNECTED:
                networkStatus.setText(R.string.status_off);
                break;
        }
    }
}