package com.example.vicaraassignment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.vicaraassignment.interfaces.NetworkReceiverInterface;
import com.example.vicaraassignment.util.NetworkUtil;

public class NetworkReceiver extends BroadcastReceiver {
    NetworkReceiverInterface listener;
    public NetworkReceiver(NetworkReceiverInterface listener) {
        this.listener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        int state = NetworkUtil.getConnectivityStatus(context);
        listener.handleNetworkState(state);
    }
}
