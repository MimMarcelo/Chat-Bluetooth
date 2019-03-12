package com.mimmarcelo.chatbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mimmarcelo.chatbluetooth.adapters.DeviceItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rcvDevices;
    private DeviceItemAdapter deviceItemAdapter;
    private List<BluetoothDevice> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        LinearLayoutManager layout = new LinearLayoutManager(this);

        devices = new ArrayList<>();

        deviceItemAdapter = new DeviceItemAdapter(this, devices);

        rcvDevices = findViewById(R.id.rcvDevices);
        rcvDevices.setLayoutManager(layout);
        rcvDevices.setAdapter(deviceItemAdapter);

        BluetoothAdapter.getDefaultAdapter().startDiscovery();
        setResult(RESULT_CANCELED);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                deviceItemAdapter.notifyDataSetChanged();
            }
        }
    };
}
