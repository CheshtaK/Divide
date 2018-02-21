package com.example.cheshta.chatapplication.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cheshta.chatapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvProfileDisplayName, tvProfileStatus, tvTotalFriends;
    Button btnSendRequest, btnDecline;
    ProgressDialog mProgressDialog;

    int mCurrentState;
    /* 0 -> Not Friends
     * 1 -> Friend Request Sent
     * 2 -> Friend Request Received
     * 3 -> Friends
     */

    DatabaseReference mUsersDatabase, mFriendReqDatabase, mFriendDatabase, mNotificationDatabase, mRootRef;
    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String id = getIntent().getStringExtra("id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvProfileDisplayName = findViewById(R.id.tvProfileDisplayName);
        tvProfileStatus = findViewById(R.id.tvProfileStatus);
        tvTotalFriends = findViewById(R.id.tvTotalFriends);
        btnSendRequest = findViewById(R.id.btnSendRequest);
        btnDecline = findViewById(R.id.btnDecline);

        mCurrentState = 0;

        btnDecline.setVisibility(View.INVISIBLE);
        btnDecline.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                tvProfileDisplayName.setText(displayName);
                tvProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.me).into(ivProfileImage);

                if(mCurrentUser.getUid().equals(id)){

                    btnDecline.setEnabled(false);
                    btnDecline.setVisibility(View.INVISIBLE);

                    btnSendRequest.setEnabled(false);
                    btnSendRequest.setVisibility(View.INVISIBLE);

                }

                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(id)){

                            String requestType = dataSnapshot.child(id).child("request_type").getValue().toString();
                            if(requestType.equals("received")){

                                mCurrentState = 2;
                                btnSendRequest.setText("Accept Friend Request");

                                btnDecline.setVisibility(View.VISIBLE);
                                btnDecline.setEnabled(true);

                            } else if(requestType.equals("sent")){

                                mCurrentState = 1;
                                btnSendRequest.setText("Cancel Friend Request");

                                btnDecline.setVisibility(View.INVISIBLE);
                                btnDecline.setEnabled(false);
                            }

                            mProgressDialog.dismiss();

                        } else {

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(id)){

                                        mCurrentState = 3;
                                        btnSendRequest.setText("Unfriend this person");

                                        btnDecline.setVisibility(View.INVISIBLE);
                                        btnDecline.setEnabled(false);
                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnSendRequest.setEnabled(false);

                // ------------------------NOT FRIENDS-------------------------

                if(mCurrentState == 0){

                    DatabaseReference newNotificationRef = mRootRef.child("notifications").child(id).push();
                    String newNotificationId = newNotificationRef.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrentUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + id + "/request_type","sent");
                    requestMap.put("Friend_req/" + id + "/" + mCurrentUser.getUid() + "/request_type","received");
                    requestMap.put("notifications/" + id + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Toast.makeText(ProfileActivity.this, "There was some error in sending the request", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                mCurrentState = 1;
                                btnSendRequest.setText("Cancel Friend Request");
                            }

                            btnSendRequest.setEnabled(true);
                        }
                    });
                }

                // ------------------------REQUEST SENT-------------------------

                if(mCurrentState == 1){

                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    btnSendRequest.setEnabled(true);
                                    mCurrentState = 0;
                                    btnSendRequest.setText("Send Friend Request");

                                    btnDecline.setVisibility(View.INVISIBLE);
                                    btnDecline.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                // ------------------------REQUEST RECEIVED-------------------------

                if(mCurrentState == 2){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + id + "/date", currentDate);
                    friendsMap.put("Friends/" + id + "/"  + mCurrentUser.getUid() + "/date", currentDate);


                    friendsMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + id, null);
                    friendsMap.put("Friend_req/" + id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                btnSendRequest.setEnabled(true);
                                mCurrentState = 3;
                                btnSendRequest.setText("Unfriend this Person");

                                btnDecline.setVisibility(View.INVISIBLE);
                                btnDecline.setEnabled(false);

                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // ------------------------FRIENDS-------------------------

                if(mCurrentState == 3){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + id, null);
                    unfriendMap.put("Friends/" + id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                mCurrentState = 0;
                                btnSendRequest.setText("Send Friend Request");

                                btnDecline.setVisibility(View.INVISIBLE);
                                btnDecline.setEnabled(false);

                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }

                            btnSendRequest.setEnabled(true);
                        }
                    });

                }
            }
        });
    }
}
