package com.mimmarcelo.chatbluetooth.adapters;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mimmarcelo.chatbluetooth.R;
import com.mimmarcelo.chatbluetooth.classes.M;

public class DeviceItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private AppCompatImageView imgDevice;
    private TextView txtDeviceName;
    private TextView txtDeviceStatus;

    private Context context;
    private BluetoothDevice device;

    public DeviceItemHolder(@NonNull View v) {
        super(v);

        imgDevice = v.findViewById(R.id.imgDevice);
        txtDeviceName = v.findViewById(R.id.txtDeviceName);
        txtDeviceStatus = v.findViewById(R.id.txtDeviceStatus);
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(M.extra.device, device);
        ((Activity)context).setResult(Activity.RESULT_OK, intent);
        ((Activity)context).finish();
    }

    public void setFields(Context context, BluetoothDevice device, int i) {
        this.context = context;
        this.device = device;
        imgDevice.setImageResource(R.drawable.ic_phone);
        txtDeviceName.setText(device.getName());
        txtDeviceStatus.setText("Disponível");
    }
}
