package com.example.vicaraassignment.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.vicaraassignment.interfaces.BluetoothReceiverInterface;

public class BluetoothReceiver extends BroadcastReceiver {
    private BluetoothReceiverInterface listener;

    public BluetoothReceiver(BluetoothReceiverInterface listener) {
        this.listener = listener;
    }

    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            listener.handleBluetoothState(state);
        }
    }
}
