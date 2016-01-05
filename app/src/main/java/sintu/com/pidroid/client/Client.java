package sintu.com.pidroid.client;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client
 */
public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Handler uiHandler;

    public Client(String serverAddress, int port, Handler uiHandler) {
        Log.d("client_debug", "Connecting as a client");
        try {
            socket = new Socket(serverAddress, port);
            Log.d("client_debug", "made socket request to: " + serverAddress + ":" + port);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String message = in.readLine();

            Message m = Message.obtain();
            m.obj = message;
            uiHandler.sendMessage(m);

            Log.d("client_debug", message);
        } catch (IOException e) {
            Log.d("client_debug", "client error: " + e.toString());
        }
    }

    public void sendImage(Drawable drawable) {

    }

}
