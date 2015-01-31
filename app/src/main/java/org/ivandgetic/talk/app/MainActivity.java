package org.ivandgetic.talk.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.io.DataOutputStream;
import java.io.IOException;


public class MainActivity extends Activity implements ObservableScrollViewCallbacks {
    public static String USERNAME = null;
    DataOutputStream out;
    ImageView compose_button_send;
    EditText compose_edit;
    String message;
    static ObservableListView observableListView;
    private long lastClickTime = 0;
    public static MessageAdapter messageAdapter;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compose_edit = (EditText) findViewById(R.id.compose_edit);
        observableListView = (ObservableListView) findViewById(R.id.listView);
        observableListView.setScrollViewCallbacks(this);
        compose_button_send = (ImageView) findViewById(R.id.compose_button_send);
        messageAdapter = new MessageAdapter(this);
        observableListView.setAdapter(messageAdapter);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(preferences.getString("username", "").length() > 0)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sign in");
            LayoutInflater inflater = this.getLayoutInflater();
            final View layout = inflater.inflate(R.layout.dialog_signin, null);
            builder.setView(layout);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor e = preferences.edit();
                    String name = ((EditText) layout.findViewById(R.id.username)).getText().toString();
                    e.putString("username", name);
                    e.apply();
                    USERNAME = preferences.getString("username", "");
                    startService(new Intent(MainActivity.this, MyService.class));

                }
            });
            builder.show();
        } else {
            USERNAME = preferences.getString("username", "");
            startService(new Intent(MainActivity.this, MyService.class));
        }
    }

    public void send(View view) {
        message = compose_edit.getText().toString();
        try {
            out = new DataOutputStream(MyService.socket.getOutputStream());
            out.writeUTF("Message:" + USERNAME + ":" + message);
            MessageAdapter.messages.add(new Message(USERNAME, message));
            messageAdapter.notifyDataSetChanged();
            observableListView.setSelection(messageAdapter.getCount());
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
                messageAdapter.notifyDataSetChanged();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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


    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getActionBar();
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }
}
