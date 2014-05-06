package org.ivandgetic.talk.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {
    public static String USERNAME = null;
    DataOutputStream out;
    ImageView compose_button_send;
    EditText compose_edit;
    String message;
    static ListView listView;
    private long lastClickTime = 0;
    public static MessageAdapter messageAdapter;
    public static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compose_edit = (EditText) findViewById(R.id.compose_edit);
        listView = (ListView) findViewById(R.id.listView);
        compose_button_send = (ImageView) findViewById(R.id.compose_button_send);
        messageAdapter = new MessageAdapter(this);
        sp = getSharedPreferences("Information", MODE_PRIVATE);
        if (!(sp.getString("name", "").length() > 0)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sign in");
            LayoutInflater inflater = this.getLayoutInflater();
            final View layout = inflater.inflate(R.layout.dialog_signin, null);
            builder.setView(layout);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor e = sp.edit();
                    String name = ((EditText) layout.findViewById(R.id.username)).getText().toString();
                    e.putString("name", name);
                    e.commit();
                    USERNAME = sp.getString("name", "");
                    startService(new Intent(MainActivity.this, MyService.class));

                }
            });
            builder.show();
        } else {
            USERNAME = sp.getString("name", "");
            startService(new Intent(MainActivity.this, MyService.class));
        }
    }


    public void send(View view) {
        message = compose_edit.getText().toString();
        try {
            out = new DataOutputStream(MyService.socket.getOutputStream());
            out.writeUTF(USERNAME + ":" + message);
            MessageAdapter.messages.add(new Message(USERNAME, message));
            listView.setAdapter(messageAdapter);
            listView.setSelection(messageAdapter.getCount());
            compose_edit.setText("");
        } catch (NullPointerException e) {
            Toast.makeText(MainActivity.this, getResources().getText(R.string.no_connected), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        compose_edit.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                MessageAdapter.messages.clear();
                listView.setAdapter(messageAdapter);
                break;
            case R.id.action_settings:
                break;
            case R.id.action_exit:
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (lastClickTime <= 0) {
            Toast.makeText(this, getResources().getText(R.string.back_again), Toast.LENGTH_SHORT).show();
            lastClickTime = System.currentTimeMillis();
        } else {
            long currentClickTime = System.currentTimeMillis();
            if (currentClickTime - lastClickTime < 2000) {
                System.exit(0);
            } else {
                Toast.makeText(this, getResources().getText(R.string.back_again), Toast.LENGTH_SHORT).show();
                lastClickTime = System.currentTimeMillis();
            }
        }
    }
}
