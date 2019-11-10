package com.example.semigo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.semigo.ui.ChatAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private ChatAdapter adapter;
//    FirebaseDatabase database;
//    DatabaseReference myRef
    public String chat_root;
    ListView listOfMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FloatingActionButton fab = findViewById(R.id.fab);
        Bundle chat_id = getIntent().getExtras();

        System.out.println(chat_id);

        if(chat_id!=null){
            chat_root = "messages/"+chat_id.get("trip_id");
        }


        displayMessages();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference().child(chat_root)
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );
                adapter.notifyDataSetChanged();
                // Clear the input
                input.setText("");
                scrollMyListViewToBottom();
            }
        });
    }
    public void displayMessages(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef;
        myRef = database.getReference(chat_root);
        final List<ChatMessage> messages = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ChatMessage message = postSnapshot.getValue(ChatMessage.class);
                    messages.add(message);

                }
                adapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        System.out.println("FUCK"+messages.size());


        listOfMessages = findViewById(R.id.list_of_messages);

        //Finally you pass them to the constructor here:
        adapter = new ChatAdapter(this,R.layout.message,messages);

        listOfMessages.setAdapter(adapter);

    }
    private void scrollMyListViewToBottom() {
        listOfMessages.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listOfMessages.setSelection(adapter.getCount() - 1);
            }
        });
    }


}
