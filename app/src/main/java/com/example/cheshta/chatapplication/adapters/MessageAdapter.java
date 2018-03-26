package com.example.cheshta.chatapplication.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cheshta.chatapplication.R;
import com.example.cheshta.chatapplication.models.Messages;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chesh on 3/23/2018.
 */


public class MessageAdapter extends RecyclerView.Adapter{

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;

    private String currentUser, chatUser;

    public MessageAdapter(Context context, List<Messages> mMessageList, String currentUser, String chatUser) {
        this.context = context;
        this.mMessageList = mMessageList;
        this.currentUser = currentUser;
        this.chatUser = chatUser;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Messages c = mMessageList.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();

        if(from_user.equals(currentUser)){
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else{
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messages message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        SentMessageHolder(View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(Messages message) {

//            String message_type = message.getType();

            tvMessage.setText(message.getMessage());
            tvTime.setText(DateUtils.formatDateTime(context,message.getTime(),DateUtils.FORMAT_SHOW_TIME));

            /*if(message_type.equals("text")) {
                tvMessage.setText(message.getMessage());
                ivMessageImage.setVisibility(View.INVISIBLE);
            }
            else {
                tvMessage.setVisibility(View.INVISIBLE);
                Picasso.with(civMessageImage.getContext()).load(message.getMessage())
                        .placeholder(R.drawable.me).into(ivMessageImage);
            }*/
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        CircleImageView civMessageImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            civMessageImage = itemView.findViewById(R.id.civMessageImage);
        }

        void bind(Messages message) {

            String from_user = message.getFrom();
            String message_type = message.getType();

            tvMessage.setText(message.getMessage());
            tvTime.setText(DateUtils.formatDateTime(context,message.getTime(),DateUtils.FORMAT_SHOW_TIME));

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    Picasso.with(civMessageImage.getContext()).load(image)
                            .placeholder(R.drawable.me).into(civMessageImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /*if(message_type.equals("text")) {
                tvMessage.setText(c.getMessage());
                viewHolder.ivMessageImage.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.tvMessage.setVisibility(View.INVISIBLE);
                Picasso.with(viewHolder.civMessageImage.getContext()).load(c.getMessage())
                        .placeholder(R.drawable.me).into(viewHolder.ivMessageImage);
            }
*/
        }
    }

}



/*
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMessage;
        public CircleImageView civMessageImage;
        public TextView tvMessageName;
        public ImageView ivMessageImage;

        public MessageViewHolder(View view) {
            super(view);

            tvMessage = view.findViewById(R.id.tvMessage);
            civMessageImage = view.findViewById(R.id.civMessageImage);
            tvMessageName = view.findViewById(R.id.tvMessageName);
            ivMessageImage = view.findViewById(R.id.ivMessageImage);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);
        String from_user = c.getFrom();
        String message_type = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.tvMessageName.setText(name);

                Picasso.with(viewHolder.civMessageImage.getContext()).load(image)
                        .placeholder(R.drawable.me).into(viewHolder.civMessageImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {
            viewHolder.tvMessage.setText(c.getMessage());
            viewHolder.ivMessageImage.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tvMessage.setVisibility(View.INVISIBLE);
            Picasso.with(viewHolder.civMessageImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.me).into(viewHolder.ivMessageImage);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
*/
