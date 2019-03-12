package com.mimmarcelo.chatbluetooth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothBroadcast extends BroadcastReceiver {

    public static final String STATUS_CONNECTED = "Connected";
    public static final String STATUS_TIMEOUT = "Time out";
    public static final String STATUS_POWER_OFF = "Power off";

    private Context context;
    private IBluetoothListener listener;
    private int prevScanMode;

    public BluetoothBroadcast(Context context) {
        this.context = context;
        if(context instanceof IBluetoothListener)
            this.listener = (IBluetoothListener)context;

        initialize();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Message m = new Message();
        // Quando o modo do scanner muda
        if (intent.getAction().equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

            int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);

            m.setCode(Message.STATUS);
            switch (scanMode){
                case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                    if(prevScanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                        m.setMessage(STATUS_TIMEOUT);
                    }
                    break;
                case BluetoothAdapter.SCAN_MODE_NONE:
                    m.setMessage(STATUS_POWER_OFF);
                    break;
                default:
                    m.setMessage("Status não analisado: "+scanMode);
            }
            prevScanMode = scanMode;
        }
        else if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
            m.setMessage(STATUS_CONNECTED);
            prevScanMode = BluetoothAdapter.STATE_CONNECTED;
        }
        if(listener != null)
            listener.bluetoothMessageReceived(m);
    }

    private void initialize() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        context.registerReceiver(this, filter);
    }
}
