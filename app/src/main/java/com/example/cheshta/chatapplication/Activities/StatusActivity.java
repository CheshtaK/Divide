package com.example.cheshta.chatapplication.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cheshta.chatapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    Toolbar statusToolbar;
    TextInputLayout tilStatus;
    Button btnSaveStatus;

    DatabaseReference mStatusDatabase;
    FirebaseUser mCurrentUser;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        
        statusToolbar = findViewById(R.id.statusToolbar);
        setSupportActionBar(statusToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilStatus = findViewById(R.id.tilStatus);
        btnSaveStatus = findViewById(R.id.btnSaveStatus);

        String statusValue = getIntent().getStringExtra("statusValue");
        tilStatus.getEditText().setText(statusValue);

        btnSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();

                String status = tilStatus.getEditText().getText().toString();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        } else {
                            Toast.makeText(StatusActivity.this, "There was some error in saving changes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
