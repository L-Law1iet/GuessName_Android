package com.guessname_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GamePage extends AppCompatActivity {
    private int PlayerNo,Players;
    private String RoomNo,TAG = "THE";
    private ListView list1;
    private String ans[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
        Intent intent = getIntent();
        PlayerNo = intent.getIntExtra("PlayerNo",0);
        Players = intent.getIntExtra("Players",0);
        RoomNo = intent.getStringExtra("RoomNo");
        ans = new String[Players];
        list1 = (ListView)findViewById(R.id.list1);
        for (int j=0;j<Players;j++){
            ans[j] = "自己";
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(int i=0;i<Players;i++){
                    if(i!=PlayerNo) {
                        ans[i] = dataSnapshot.child(RoomNo).child(String.valueOf(i)).child("玩家id").getValue(String.class);
                        ans[i] += "的答案是：";
                        ans[i] += dataSnapshot.child(RoomNo).child(String.valueOf(i)).child("自己答案").getValue(String.class);
                        setAda();
                    }
                    else{
                        ans[i] = dataSnapshot.child(RoomNo).child(String.valueOf(i)).child("玩家id").getValue(String.class);
                        ans[i] += "的答案是：自己猜";
                        setAda();
                    }
                    Log.d(TAG,ans[i]);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    private void setAda() {
        ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,ans);
        list1.setAdapter(adapter);
    }

}