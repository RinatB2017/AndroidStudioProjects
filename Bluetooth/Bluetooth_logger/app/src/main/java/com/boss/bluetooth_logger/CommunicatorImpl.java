package com.boss.bluetooth_logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class CommunicatorImpl extends Thread implements Communicator {

    interface CommunicationListener {
        void onMessage(String message);
    }

    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final CommunicationListener listener;

    public CommunicatorImpl(BluetoothSocket socket, CommunicationListener listener) {
        this.socket = socket;
        this.listener = listener;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.d("CommunicatorImpl", e.getLocalizedMessage());
        }
        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    @Override
    public void startCommunication() {
        byte[] buffer = new byte[1024];

        int bytes;

        Log.d("CommunicatorImpl", "Run the communicator");

        while (true) {
            try {
                bytes = inputStream.read(buffer);
                Log.d("CommunicatorImpl", "Read " + bytes + " bytes");
                if (listener != null) {
                    listener.onMessage(new String(buffer).substring(0, bytes));
                }
            } catch (IOException e) {
                Log.d("CommunicatorImpl", e.getLocalizedMessage());
                break;
            }
        }
    }

    public void write(String message) {
        try {
            Log.d("CommunicatorImpl", "Write " + message);
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            Log.d("CommunicatorImpl", e.getLocalizedMessage());
        }
    }

    @Override
    public void stopCommunication() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.d("CommunicatorImpl", e.getLocalizedMessage());
        }
    }

}
