package com.example.vicaraassignment.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.vicaraassignment.MainActivity;
import com.example.vicaraassignment.R;
import com.example.vicaraassignment.interfaces.BluetoothReceiverInterface;
import com.example.vicaraassignment.interfaces.NetworkReceiverInterface;
import com.example.vicaraassignment.receiver.BluetoothReceiver;
import com.example.vicaraassignment.receiver.NetworkReceiver;
import com.example.vicaraassignment.util.NetworkUtil;

import static com.example.vicaraassignment.App.CHANNEL_ID;

public class NotificationService extends Service implements BluetoothReceiverInterface, NetworkReceiverInterface {

    private static final String TAG = NotificationService.class.getSimpleName();

    private BluetoothReceiver mBluetoothReceiver;
    private NetworkReceiver mNetworkReceiver;
    private PendingIntent pendingIntent;

    int bluetoothStatus, networkStatus;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        mBluetoothReceiver = new BluetoothReceiver(this);
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        mNetworkReceiver = new NetworkReceiver(this);
        registerReceiver(mNetworkReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        bluetoothStatus = R.string.status_off;
        networkStatus = R.string.status_off;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        bluetoothStatus = intent.getIntExtra("bluetoothStatus", R.string.status_off);
        addNotification();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);
        unregisterReceiver(mNetworkReceiver);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void handleBluetoothState(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                bluetoothStatus = R.string.status_off;
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                bluetoothStatus = R.string.status_turning_off;
                break;
            case BluetoothAdapter.STATE_ON:
                bluetoothStatus = R.string.status_on;
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                bluetoothStatus = R.string.status_turning_on;
                break;
        }
        addNotification();
    }

    @Override
    public void handleNetworkState(int state) {
        switch (state) {
            case NetworkUtil.TYPE_WIFI:
                networkStatus = R.string.wifi_on;
                break;
            case NetworkUtil.TYPE_MOBILE:
                networkStatus = R.string.mobile_data_on;
                break;
            case NetworkUtil.TYPE_NOT_CONNECTED:
                networkStatus = R.string.status_off;
                break;
        }
        addNotification();
    }

    private void addNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Bluetooth " + getText(bluetoothStatus) + "\nNetwork: " + getText(networkStatus)))
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }
}
