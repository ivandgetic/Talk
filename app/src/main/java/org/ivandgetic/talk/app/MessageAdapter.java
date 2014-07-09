package org.ivandgetic.talk.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    public static List<Message> messages = new ArrayList<Message>();
    public Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Message messageItem = getItem(i);
        if (messageItem.getName().equals(MainActivity.USERNAME)) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.message_right, null);
        } else view = LayoutInflater.from(getContext()).inflate(R.layout.message_left, null);
        TextView message = (TextView) view.findViewById(R.id.textView);
        message.setText(messageItem.getName() + ": " + messageItem.getMessage());
        return view;
    }
}
