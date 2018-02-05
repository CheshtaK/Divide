package com.example.cheshta.chatapplication.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.cheshta.chatapplication.Models.Users;
import com.example.cheshta.chatapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllUsersActivity extends AppCompatActivity {
    
    Toolbar usersToolbar;
    RecyclerView rvUsers;

    DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
    
        usersToolbar = findViewById(R.id.usersToolbar);
        setSupportActionBar(usersToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setHasFixedSize(true);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.single_user_layout,
                UsersViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                viewHolder.setDisplayName(model.getName());
                viewHolder.setUserStatus(model.getStatus());
            }
        };

        rvUsers.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name){
            TextView tvUsersName = mView.findViewById(R.id.tvUsersName);
            tvUsersName.setText(name);
        }

        public void setUserStatus(String status){
            TextView tvUsersStatus = mView.findViewById(R.id.tvUsersStatus);
            tvUsersStatus.setText(status);
        }
    }
}
