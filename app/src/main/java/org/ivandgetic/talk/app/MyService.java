package org.ivandgetic.talk.app;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class MyService extends Service {
    private static final int SOCKET_PORT = 50000;
    public static String SOCKET_ADDRESS = "192.168.137.1";
    public static Socket socket;
    DataInputStream in;
    SharedPreferences sharedPreferences;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SOCKET_ADDRESS=sharedPreferences.getString("server_address","");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(SOCKET_ADDRESS, SOCKET_PORT);
                    in = new DataInputStream(socket.getInputStream());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    final String line = in.readUTF();
                                    MainActivity.listView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            String[] separate = line.split(":", 2);
                                            String name = separate[0];
                                            String message = separate[1];
                                            if (!name.equals(MainActivity.USERNAME)) {
                                                MessageAdapter.messages.add(new Message(name, message));
                                                MainActivity.listView.setAdapter(MainActivity.messageAdapter);
                                                MainActivity.listView.setSelection(MainActivity.messageAdapter.getCount());
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, getResources().getText(R.string.server_shutdown), Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
