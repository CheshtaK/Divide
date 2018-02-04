package com.example.cheshta.chatapplication.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.cheshta.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    DatabaseReference mUserDatabase;
    FirebaseUser mcurrentUser;

    CircleImageView civSettingsImage;
    TextView tvSettingsName, tvSettingsStatus;
    Button btnChangeName, btnChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        civSettingsImage = findViewById(R.id.civSettingsImage);
        tvSettingsName = findViewById(R.id.tvSettingsName);
        tvSettingsStatus = findViewById(R.id.tvSettingsStatus);
        btnChangeName = findViewById(R.id.btnChangeName);
        btnChangeStatus = findViewById(R.id.btnChangeStatus);

        mcurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = mcurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                tvSettingsName.setText(name);
                tvSettingsStatus.setText(status);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
