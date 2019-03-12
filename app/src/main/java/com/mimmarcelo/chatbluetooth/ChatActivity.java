package com.mimmarcelo.chatbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mimmarcelo.chatbluetooth.adapters.MessageItemAdapter;
import com.mimmarcelo.chatbluetooth.bluetooth.BluetoothBroadcast;
import com.mimmarcelo.chatbluetooth.bluetooth.BluetoothManager;
import com.mimmarcelo.chatbluetooth.bluetooth.IBluetoothListener;
import com.mimmarcelo.chatbluetooth.bluetooth.Message;
import com.mimmarcelo.chatbluetooth.classes.M;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener, IBluetoothListener {


    private AppCompatButton btnSendMessage;
    private TextInputLayout tilMessage;
    private AppCompatEditText edtMessage;
    private RecyclerView rcvMessages;

    private BluetoothBroadcast bluetoothBroadcast;
    private BluetoothManager bluetoothManager;

    private List<String> messages;
    private MessageItemAdapter adapter;

    private boolean selecteds[];

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendMessage:
                sendMessageClick();
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selecteds[which] = isChecked;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case AlertDialog.BUTTON_POSITIVE:
                bluetoothManager.desconnect(selecteds);
                if(bluetoothManager.connectionsNames().length == 0){
                    finish();
                }
                break;

        }
    }

    @Override
    public void bluetoothMessageReceived(Message message) {
        switch (message.getCode()) {
            case Message.STATUS:
                switch (message.getMessage()) {
                    case BluetoothBroadcast.STATUS_CONNECTED:
                        enableFields(true);
                        break;
                    case BluetoothBroadcast.STATUS_TIMEOUT:
                    case BluetoothBroadcast.STATUS_POWER_OFF:
                        finish();
                        break;
                }
                break;
            case Message.MESSAGE:
                messages.add(message.getMessage());
                adapter.notifyDataSetChanged();
                rcvMessages.smoothScrollToPosition(messages.size()-1);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuOpenService:
                openService();
                break;
            case R.id.menuDesconnect:
                desconect();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializeComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothManager.finhishThread();
        bluetoothManager = null;
        unregisterReceiver(bluetoothBroadcast);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case M.requestCode.openService:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                } else {
                    bluetoothManager.openService();
                }
                break;
            case M.requestCode.search:
                if (resultCode == RESULT_OK) {
                    BluetoothDevice d = data.getParcelableExtra(M.extra.device);
                    bluetoothManager.connect(d.getAddress());
                }
                else {
                    finish();
                }
                break;
        }
    }

    private void sendMessageClick() {
        if (validadeMessage()) {
            bluetoothManager.sendMessage(BluetoothAdapter.getDefaultAdapter().getName()+":;"+edtMessage.getText().toString());
            edtMessage.setText("");
        }
    }

    private void desconect(){
        AlertDialog.Builder popup = new AlertDialog.Builder(this);
        if(bluetoothManager.isServer()) {
            String[] names = bluetoothManager.connectionsNames();
            if(names.length > 1) {
                selecteds = new boolean[names.length];
                popup.setTitle("Desconectar de:");
                popup.setMultiChoiceItems(names, null, this);
                popup.setNegativeButton("CANCELAR", this);
                popup.setPositiveButton("DESCONECTAR", this);
                popup.create().show();
                return;
            }
        }

        selecteds = new boolean[]{true};
        popup.setMessage("Encerrar conexão");
        popup.setNegativeButton("CANCELAR", this);
        popup.setPositiveButton("DESCONECTAR", this);
        popup.create().show();
    }

    private void openService(){
        //SOLICITAR QUE PERMITA SEU DISPOSITIVO FIQUE VISÍVEL PARA OUTREM
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20);//POR 10 SEGUNDOS
        startActivityForResult(intent, M.requestCode.openService);
    }

    private void initializeComponents() {
        bluetoothBroadcast = new BluetoothBroadcast(this);
        bluetoothManager = new BluetoothManager(this);

        messages = new ArrayList<>();
        adapter = new MessageItemAdapter(messages);

        btnSendMessage = findViewById(R.id.btnSendMessage);

        tilMessage = findViewById(R.id.tilMessage);
        edtMessage = findViewById(R.id.edtMessage);

        rcvMessages = findViewById(R.id.rcvHistory);

        if(getIntent().hasExtra(M.extra.isServer)){
            openService();
        }
        else{
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, M.requestCode.search);
        }

        btnSendMessage.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        rcvMessages.setLayoutManager(layoutManager);
        rcvMessages.setAdapter(adapter);
        rcvMessages.setHasFixedSize(true);
        enableFields(false);
    }

    private void enableFields(boolean enabled) {
        edtMessage.setEnabled(enabled);
        btnSendMessage.setEnabled(enabled);
    }

    private boolean validadeMessage() {
        if (edtMessage.getText().toString().length() == 0) {
            tilMessage.setError(getString(R.string.texto_vazio));
        } else if (edtMessage.getText().toString().length() > 30) {
            tilMessage.setError(getString(R.string.texto_longo));
        } else {
            tilMessage.setErrorEnabled(false);
            return true;
        }
        tilMessage.setErrorEnabled(true);
        return false;
    }

}
