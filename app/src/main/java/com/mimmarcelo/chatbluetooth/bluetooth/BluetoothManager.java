package com.mimmarcelo.chatbluetooth.bluetooth;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BluetoothManager implements Serializable {

    private IBluetoothListener listener;
    private List<ConnectionThread> threads;
    private final Context context;

    private final String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private String serverMac;
    private boolean server;
    private int operation;

    public BluetoothManager(Context context) {
        this.threads = new ArrayList<>();
        this.context = context;
        if (context instanceof IBluetoothListener) {
            this.listener = (IBluetoothListener) context;
        }
    }

    public void connect(String serverMac) {
        this.serverMac = serverMac;
        this.operation = ConnectionThread.CONNECT_TO_SERVER;
        this.server = false;
        startThread();
    }

    protected int getOperation() {
        return this.operation;
    }

    protected String getServerMac() {
        return this.serverMac;
    }

    protected String getUUID() {
        return this.UUID;
    }

    protected void setOperation(int operation) {
        this.operation = operation;
    }

    protected void messageReceived(Message message) {
        try{
            if (server) {
                sendMessage(message);
            } else {
                listener.bluetoothMessageReceived(message);
            }
        }
        catch (Exception e){
            listener.bluetoothMessageReceived(message);
        }
    }

    public void openService() {
        this.operation = ConnectionThread.CONNECT_TO_CLIENT;
        this.server = true;
        startThread();
    }

    public void sendMessage(Message message) {
        for (ConnectionThread t: threads) {
            t.enviarMensagem(message.getMessage());
        }
        if (server) {
            listener.bluetoothMessageReceived(message);
        }
    }

    public void sendMessage(String message){
        Message m = new Message();
        m.setCode(Message.MESSAGE);
        m.setMessage(message);
        sendMessage(m);
    }

    private void startThread(){
        finhishThread();
        ConnectionThread thread = new ConnectionThread(context, this);
        thread.start();
        threads.add(thread);
    }

    public void finhishThread() {
        if(!server) {
            if (!this.threads.isEmpty()) {
                for (ConnectionThread t : threads) {
                    t.cancel();
                    t = null;
                }
            }
        }
    }

    public boolean isServer() {
        return server;
    }

    public String[] connectionsNames(){
        String names[] = new String[threads.size()];
        for (int i = 0; i < threads.size(); i++){
            names[i] = threads.get(i).getName();
        }
        return names;
    }

    public void desconnect(boolean[] selecteds) {
        for (int i = 0; i < selecteds.length; i++){
            if(selecteds[i]){
                threads.get(i).cancel();
                threads.remove(i);
            }
        }
    }
}
