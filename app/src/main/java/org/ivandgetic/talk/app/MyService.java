package org.ivandgetic.talk.app;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MyService extends Service {
    private static final int SOCKET_PORT = 50000;
    public static String SOCKET_ADDRESS=null;
    public static Socket socket;
    DataInputStream in;
    DataOutputStream out;
    SharedPreferences sharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SOCKET_ADDRESS = sharedPreferences.getString("server_address", "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(SOCKET_ADDRESS, SOCKET_PORT);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(MyService.socket.getOutputStream());
                    out.writeUTF("Operate:GetAllMessage");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    final String line = in.readUTF();
                                    MainActivity.observableListView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            String[] separate = line.split(":", 3);
                                            if (separate[0].equals("Message")) {
                                                if (!separate[1].equals(MainActivity.USERNAME)) {
                                                    MessageAdapter.messages.add(new Message(separate[1], separate[2]));
                                                    MainActivity.messageAdapter.notifyDataSetChanged();
                                                    MainActivity.observableListView.setSelection(MainActivity.messageAdapter.getCount());
                                                }
                                            }else if (separate[0].equals("Operate")) {
                                                MessageAdapter.messages.add(new Message(separate[1], separate[2]));
                                                MainActivity.messageAdapter.notifyDataSetChanged();
                                                MainActivity.observableListView.setSelection(MainActivity.messageAdapter.getCount());
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
