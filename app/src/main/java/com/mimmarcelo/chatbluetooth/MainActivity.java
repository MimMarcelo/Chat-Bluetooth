package com.mimmarcelo.chatbluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;

import com.mimmarcelo.chatbluetooth.classes.M;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener {

    //Componentes do layout
    private SwitchCompat swtBluetooth;
    private SwitchCompat swtOpenService;
    private AppCompatButton btnSearchService;

    private BluetoothAdapter antena;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearchService:
                searchClick();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        switch (v.getId()) {
            case R.id.swtBluetooth:
                bluetoothSwitch();
                break;
            case R.id.swtOpenService:
                openServiceClick(isChecked);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int codigoDaRequisicao, String listaDePermissoes[], int[] listaDeConsessao) {
        switch (codigoDaRequisicao) {
            case M.requestCode.permissions: {
                if (!(listaDeConsessao.length > 0 && listaDeConsessao[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Permissão negada
                } else {
                    initializeComponents();
                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermissions()) {
            initializeComponents();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case M.requestCode.bluetooth:
                changeSwitch(swtBluetooth, antena.isEnabled());
                break;
            case M.requestCode.openService:
            case M.requestCode.search:
                changeSwitch(swtBluetooth, antena.isEnabled());
                changeSwitch(swtOpenService, false);
                break;
//            case M.requestCode.search:
//                if (resultCode == RESULT_OK) {
//                    data.setClass(this, ChatActivity.class);
//                    startActivity(data);
//                }
//                break;
        }
    }

    private void bluetoothSwitch() {
        if (antena.isEnabled()) {
            antena.disable();
            changeSwitch(swtBluetooth, false);
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, M.requestCode.bluetooth);
        }
    }

    private void searchClick() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivityForResult(intent, M.requestCode.search);
    }

    private void openServiceClick(boolean isChecked) {
        if (antena.isEnabled() && isChecked) {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra(M.extra.isServer, true);
            startActivityForResult(i, M.requestCode.openService);
        } else {
            antena.cancelDiscovery();
            changeSwitch(swtOpenService, false);
        }
    }

    private void initializeComponents() {
        antena = BluetoothAdapter.getDefaultAdapter();

        swtBluetooth = findViewById(R.id.swtBluetooth);
        swtOpenService = findViewById(R.id.swtOpenService);

        btnSearchService = findViewById(R.id.btnSearchService);

        changeSwitch(swtBluetooth, antena.isEnabled());
        changeSwitch(swtOpenService, false);

        btnSearchService.setOnClickListener(this);
    }

    private void changeSwitch(SwitchCompat swt, boolean checked) {
        swt.setOnCheckedChangeListener(null);
        swt.setChecked(checked);
        swt.setOnCheckedChangeListener(this);
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, M.requestCode.permissions);
                return false;
            }
        }
        return true;
    }

}
