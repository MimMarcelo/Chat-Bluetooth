package com.mimmarcelo.chatbluetooth.adapters;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.mimmarcelo.chatbluetooth.R;

public class MessageItemHolder extends RecyclerView.ViewHolder {

    private TextView txtSender;
    private TextView txtMessage;
    private View lltMessage;

    public MessageItemHolder(@NonNull View v) {
        super(v);
        lltMessage = v.findViewById(R.id.lltMessage);
        txtSender = v.findViewById(R.id.txtSender);
        txtMessage = v.findViewById(R.id.txtMessage);
    }

    public void setFields(String message) {
        String m[] = message.split(":;");
        if(message.startsWith(BluetoothAdapter.getDefaultAdapter().getName())){
            lltMessage.setBackgroundColor(lltMessage.getResources().getColor(R.color.secondaryDarkColor));
            txtSender.setGravity(Gravity.RIGHT);
            txtMessage.setGravity(Gravity.RIGHT);
            txtSender.setText("Me");
        }
        else {
            txtSender.setText(m[0]);
        }
            m[1] = m[1].split("\n")[0];
        txtMessage.setText(m[1]);
    }
}
