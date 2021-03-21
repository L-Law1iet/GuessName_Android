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

public class SetName extends AppCompatActivity {
    private String RoomNo,TAG = "THE TAG",giveId;
    private EditText edtID, edtQues;
    private Button btnCheck1, btnCheck2;
    private TextView txtRoomNo, txtPeople,txtQues,txtWho,txtWait,txtGive;
    private int PlayerNo,Players,Peoples,GM,Ready1 = 0,Ready2 = 0,giveAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);
        edtID = (EditText) findViewById(R.id.EdtID);
        edtQues = (EditText) findViewById(R.id.EdtQues);
        btnCheck1 = (Button) findViewById(R.id.btnCheck1);
        btnCheck2 = (Button) findViewById(R.id.btnCheck2);
        txtRoomNo = (TextView) findViewById(R.id.txtRoomNo);
        txtPeople = (TextView) findViewById(R.id.txtPeople);
        txtQues = (TextView) findViewById(R.id.TxtQues);
        txtWho = (TextView) findViewById(R.id.txtWho);
        txtWait = (TextView) findViewById(R.id.txtWait);
        txtGive = (TextView) findViewById(R.id.txtGive);

        Intent intent = getIntent();
        RoomNo = intent.getStringExtra("ROOM");
        GM = intent.getIntExtra("GMs",0);
        txtRoomNo.setText(RoomNo);
        btnCheck1.setOnClickListener(btnCheck1Listener);
        btnCheck2.setOnClickListener(btnCheck2Listener);

        basicRead();
    }

    private View.OnClickListener btnCheck1Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            int peoples1 = Peoples;
            PlayerNo = peoples1;
            myRef.child(RoomNo).child(String.valueOf(PlayerNo)).child("玩家id").setValue(String.valueOf(edtID.getText()));
            peoples1++;
            myRef.child(RoomNo).child("加入人數").setValue(String.valueOf(peoples1));
            btnCheck1.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener btnCheck2Listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            myRef.child(RoomNo).child(String.valueOf(PlayerNo)).child("玩家出題").setValue(String.valueOf(edtQues.getText()));
            int peoples2 = giveAns;
            peoples2++;
            myRef.child(RoomNo).child("出題人數").setValue(String.valueOf(peoples2));
            btnCheck2.setVisibility(View.INVISIBLE);

            myRef.child(RoomNo).child(giveId).child("自己答案").setValue(String.valueOf(edtQues.getText()));
        }
    };
    private void basicRead(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Peoples = Integer.valueOf(dataSnapshot.child(RoomNo).child("加入人數").getValue(String.class));
                Players = Integer.valueOf(dataSnapshot.child(RoomNo).child("遊戲人數").getValue(String.class));
                giveAns = Integer.valueOf(dataSnapshot.child(RoomNo).child("出題人數").getValue(String.class));
                txtPeople.setText(String.valueOf(Peoples));
                txtGive.setText(String.valueOf(giveAns));

                if(Peoples == Players & GM == 1 & Ready1 == 0){
                    int count = Players;
                    int ques[] = new int[count];
                    int Length = ques.length;
                    for(int i = 0; i<Length; i++){
                        ques[i] = i;
                        Log.d(TAG,"first："+ques[i]);
                    }
                    int Random, temp;
                    for(int i = 0; i<Length; i++){
                        while (ques[i] == i){
                            Random = (int) (Math.random() * Length);
                            temp = ques[i];
                            ques[i] = ques[Random];
                            ques[Random] = temp;
                        }
                        Log.d(TAG,"second："+ques[i]);
                    }
                    for(int i = 0; i<count; i++){
                        String ForId = dataSnapshot.child(RoomNo).child(String.valueOf(ques[i])).child("玩家id").getValue(String.class);
                        myRef.child(RoomNo).child(String.valueOf(i)).child("給題者").setValue(ForId);
                        myRef.child(RoomNo).child(String.valueOf(i)).child("給題id").setValue(String.valueOf(ques[i]));
                        myRef.child(RoomNo).child("準備完了").setValue("1");
                        Log.d(TAG,"third："+ques[i]);
                    }
                    Ready1 = 1;
                }
                Ready1 = Integer.valueOf(dataSnapshot.child(RoomNo).child("準備完了").getValue(String.class));
                if(Ready1 == 1){
                    txtWho.setText(dataSnapshot.child(RoomNo).child(String.valueOf(PlayerNo)).child("給題者").getValue(String.class));
                    giveId = dataSnapshot.child(RoomNo).child(String.valueOf(PlayerNo)).child("給題id").getValue(String.class);
                    txtQues.setVisibility(View.VISIBLE);
                    edtQues.setVisibility(View.VISIBLE);
                    btnCheck2.setVisibility(View.VISIBLE);
                    txtWait.setText("等待出題人數");
                    txtPeople.setVisibility(View.INVISIBLE);
                    txtGive.setVisibility(View.VISIBLE);
                }

                if(giveAns == Players & GM == 1){
                    myRef.child(RoomNo).child("出完題了").setValue("1");
                    Ready2 = 1;
                }

                Ready2 = Integer.valueOf(dataSnapshot.child(RoomNo).child("出完題了").getValue(String.class));
                if(Ready2 == 1){
                    Intent it = new Intent(SetName.this,GamePage.class);
                    it.putExtra("PlayerNo",PlayerNo);
                    it.putExtra("Players",Players);
                    it.putExtra("RoomNo",RoomNo);
                    startActivity(it);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
}