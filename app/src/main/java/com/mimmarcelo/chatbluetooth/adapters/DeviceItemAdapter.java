package com.mimmarcelo.chatbluetooth.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mimmarcelo.chatbluetooth.R;

import java.util.List;

public class DeviceItemAdapter extends RecyclerView.Adapter<DeviceItemHolder> {

    Context context;
    List<BluetoothDevice> devices;

    public DeviceItemAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public DeviceItemHolder onCreateViewHolder(@NonNull ViewGroup view, int i) {
        return new DeviceItemHolder(LayoutInflater.from(view.getContext()).inflate(R.layout.item_device, view, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceItemHolder holder, int i) {
        final BluetoothDevice device = devices.get(i);

        holder.setFields(context, device, i);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

}
