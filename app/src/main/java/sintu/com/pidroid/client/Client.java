package sintu.com.pidroid.client;

import java.io.BufferedReader;

import android.graphics.Bitmap;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;


import java.net.Socket;

/**
 * Client
 */
public class Client {

    private BufferedReader in;
    private Socket socket;
    private Handler uiHandler;
    private OutputStream out;


    public Client(String serverAddress, int port, Handler uiHandler) {
        Log.d("client_debug", "Connecting as a client");
        try {
            socket = new Socket(serverAddress, port);
            Log.d("client_debug", "made socket request to: " + serverAddress + ":" + port);
            out = socket.getOutputStream();
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String message = in.readLine();

            Message m = Message.obtain();
            m.obj = message;
            uiHandler.sendMessage(m);

            Log.d("client_debug", message);
        } catch (IOException e) {
            Log.d("client_debug", "client error: " + e.toString());
        }
    }

    public void sendImage(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            out.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
