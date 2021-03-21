package com.guessname_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private EditText edtPlayer,edtRoom;
    private Button btnNew,btnJoin;
    private String TAG = "",value1,value2;
    private int GM = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtPlayer = (EditText)findViewById(R.id.EdtPlayer);
        edtRoom = (EditText)findViewById(R.id.EdtRoom);
        btnNew = (Button)findViewById(R.id.BtnNew);
        btnJoin = (Button)findViewById(R.id.BtnJoin);
        btnNew.setOnClickListener(btnNewListener);
        btnJoin.setOnClickListener(btnJoinListener);

    }

    private View.OnClickListener btnNewListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Random random = new Random();
            int roomNo = random.nextInt(8999)+1000;
            String rooms = String.valueOf(roomNo);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            myRef.child(rooms).child("遊戲人數").setValue(String.valueOf(edtPlayer.getText()));
            myRef.child(rooms).child("房間號").setValue(rooms);
            myRef.child(rooms).child("加入人數").setValue("0");
            myRef.child(rooms).child("準備完了").setValue("0");
            myRef.child(rooms).child("出題人數").setValue("0");
            myRef.child(rooms).child("出完題了").setValue("0");
            Intent it = new Intent(MainActivity.this,SetName.class);
            it.putExtra("ROOM",rooms);
            it.putExtra("GMs",GM);
            startActivity(it);
        }
    };

    private View.OnClickListener btnJoinListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    value2 = String.valueOf(edtRoom.getText());
                    value1 = dataSnapshot.child(value2).child("房間號").getValue(String.class);
                    if(value1.equals(value2)){
                        Intent it = new Intent(MainActivity.this,SetName.class);
                        it.putExtra("ROOM",value2);
                        GM = 0;
                        it.putExtra("GMs",GM);
                        startActivity(it);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

        }
    };

}