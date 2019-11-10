package com.example.semigo.ui;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.semigo.ChatMessage;
import com.example.semigo.R;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    private int resourceLayout;
    private Context mContext;

    public ChatAdapter(Context context, int resource, List<ChatMessage> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        ChatMessage p = getItem(position);

        if (p != null) {
            TextView messageText = v.findViewById(R.id.message_text);
            TextView messageTime = v.findViewById(R.id.message_time);
            TextView messageUser= v.findViewById(R.id.message_user);

            messageText.setText(p.getMessageText());
            messageUser.setText(p.getMessageUser());
            // Format the date before showing it
            messageTime.setText(DateFormat.format("HH:mm:ss", p.getMessageTime()));
        }

        return v;
    }

}