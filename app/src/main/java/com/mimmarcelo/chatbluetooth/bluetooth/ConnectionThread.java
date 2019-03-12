package com.mimmarcelo.chatbluetooth.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class ConnectionThread extends Thread {

    protected static final int CONNECT_TO_SERVER = 1;
    protected static final int CONNECT_TO_CLIENT = 2;
    protected static final int CONNECTED = 3;

    private final Context context;
    private final BluetoothManager manager;

    private BluetoothSocket socketBluetooth = null;
    private InputStream input = null;
    private OutputStream output = null;

    boolean running = false;

    public ConnectionThread(Context context, BluetoothManager manager) {
        this.context = context;
        this.manager = manager;
    }

    /**
     * MÉTODO QUE INICIA E CONTROLA A CONEXÃO
     */
    @Override
    public void run() {
        Message m = new Message();
        switch (manager.getOperation()) {
            case CONNECT_TO_CLIENT:
                try {
                    BluetoothServerSocket servidorBluetooth = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("nome do app", UUID.fromString(manager.getUUID()));
                    socketBluetooth = servidorBluetooth.accept();

                    if (socketBluetooth != null) {
                        servidorBluetooth.close();
                    }
                } catch (IOException e) {
                    m.setCode(Message.ERROR);
                    m.setMessage("Tempo para conexão esgotado");
                    toMainActivity(m);
                }
                break;
            case CONNECT_TO_SERVER:
                try {
                    BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(manager.getServerMac());
                    socketBluetooth = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(manager.getUUID()));

                    if (socketBluetooth != null) {
                        socketBluetooth.connect();
                    }

                } catch (IOException e) {
                    m.setCode(Message.ERROR);
                    m.setMessage(e.getMessage());
                    toMainActivity(m);
                }
                break;
            default:
                m.setCode(Message.ERROR);
                m.setMessage("Opção de conexão inválida!");
                toMainActivity(m);
                break;
        }

        if (socketBluetooth != null) {
            manager.setOperation(CONNECTED);

            setName(socketBluetooth.getRemoteDevice().getName());

            this.running = true;

            try {
                input = socketBluetooth.getInputStream();
                output = socketBluetooth.getOutputStream();

                byte[] buffer = new byte[1024];
                int bytes;
                m.setCode(Message.MESSAGE);

                while (running) {
                    bytes = input.read(buffer);
                    m.setMessage(Arrays.copyOfRange(buffer, 0, bytes));
                    toMainActivity(m);
                }

            } catch (IOException e) {
                m.setCode(Message.ERROR);
                m.setMessage(e.getMessage());
                toMainActivity(m);
            }
        }
        cancel();
    }

    /**
     * ENVIA MENSAGEM PARA A ACTIVITY QUE ACIONOU A CONEXÃO
     *
     * @param data MENSAGEM RECEBIDA
     */
    private void toMainActivity(final Message data) {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                manager.messageReceived(data);
            }
        });
    }

    /**
     * CONVERTE A MENSAGEM EM BYTES E ENVIA AO DESTINATÁRIO
     *
     * @param mensagem MENSAGEM A SER ENVIADA
     */
    public void enviarMensagem(String mensagem) {

        if(!mensagem.endsWith("\n"))
            mensagem += "\n";

        Message m = new Message();
        byte[] data = mensagem.getBytes();

        if (output != null) {
            try {
                //TRANSMITE A MENSAGEM
                output.write(data);
                output.flush();
                //toMainActivity(m);
            } catch (IOException e) {
                m.setCode(Message.ERROR);
                m.setMessage(e.getMessage());
                toMainActivity(m);
            }
        } else {
            m.setCode(Message.ERROR);
//            m.setMessage("Stream de saída indisponível");
//            toMainActivity(m);
        }
    }

    /**
     * ENCERRA A THREAD DE CONEXÃO
     */
    public void cancel() {
        if (input != null) {
            try {
                input.close();
            } catch (Exception e) {
            }
            input = null;
        }

        if (output != null) {
            try {
                output.close();
            } catch (Exception e) {
            }
            output = null;
        }

        if (socketBluetooth != null) {
            try {
                socketBluetooth.close();
            } catch (Exception e) {
            }
            socketBluetooth = null;
        }

        running = false;
    }

}
