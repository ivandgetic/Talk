package org.ivandgetic.talk.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class MyService extends Service {
    private static final int SOCKET_PORT = 50000;
    private static final String SOCKET_ADDRESS = "192.168.137.1";
    public static Socket socket;
    DataInputStream in;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
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
                                            String name = null, message = null;
                                            for (int i = 0; i <= line.length(); i++) {
                                                if (line.charAt(i) == ':') {
                                                    name = line.substring(0, i);
                                                    message = line.substring(i + 1, line.length());
                                                    break;
                                                }
                                            }
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
