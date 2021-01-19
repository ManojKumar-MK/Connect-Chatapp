package developermk.chatapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developermk.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import developermk.chatapp.model.Chatlist;
import developermk.chatapp.model.chat.LastChats;
import developermk.chatapp.service.FirebaseService;
import developermk.chatapp.view.activities.chats.ChatsActivity;
import developermk.chatapp.view.activities.dialog.DialogViewUser;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
    private List<Chatlist> list;
    private Context context;
    private boolean ischat;
    private DatabaseReference chatRef1,messageRef;
    private List<String> listlast = new ArrayList<>();
    private FirebaseService service;
    private FirebaseUser user;
    private String theLastMessage,mCurrentUser;

    public ChatListAdapter() {
    }

    public ChatListAdapter(List<Chatlist> list, Context context, boolean ischat) {
        this.list = list;
        this.context = context;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        user=FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser=user.getUid();


        chatRef1=FirebaseDatabase.getInstance().getReference();
        messageRef=FirebaseDatabase.getInstance().getReference();

        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        final Chatlist chatlist = list.get(position);


        holder.tvName.setText(chatlist.getUserName());



      //  holder.tvDate.setText(chatlist.getDate());

        Log.d("Check","UserID "+chatlist.getUserID());



        chatRef1.child("Users").child(chatlist.getUserID().toString()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    if(snapshot.child("userState").hasChild("state")) {
                        String state = snapshot.child("userState").child("state").getValue().toString();
//                        String date = snapshot.child("userState").child("date").getValue().toString();
  //                      String time = snapshot.child("userState").child("time").getValue().toString();
                        if (state.equals("online")) {

                           // holder.btnStatus.getDrawable().setTint(R.color.online);
                            holder.btnStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_adjust_24));
                            holder.btnStatus.getDrawable().setTint(context.getResources().getColor(R.color.online));
                             holder.tvDate.setText("Online");
                            holder.tvDate.setTextColor(R.color.online);
                        }
                        else if (state.equals("offline")) {
                            holder.btnStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_adjust_24));
                            holder.btnStatus.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_dark));
                            holder.tvDate.setText("Offline");
                            holder.tvDate.setTextColor(R.color.offline);
                        }
                    }
                        else {

                       // holder.btnStatus.getDrawable().setTint(R.color.offline);

                        holder.tvDate.setText("Offline");
                        holder.tvDate.setTextColor(R.color.offline);
                        }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if(ischat)
        {
            lastmessage(chatlist.getUserID(),holder.tvDesc);

        }
        else {
            holder.tvDesc.setText(chatlist.getDescription());
        }

    /*    messageRef.child("Chats").orderByKey().getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {

                    //Log.d("Check","Value"+snapshot1.getValue());
                    LastChats chats = snapshot1.getValue(LastChats.class);
                    if (chats != null && chats.getSender().equals(user.getUid()) && chats.getReceiver().equals(chatlist.getUserID())
                            || chats.getReceiver().equals(user.getUid()) && chats.getSender().equals(chatlist.getUserID())
                    )
                    {
                        listlast.add(chats.getLastMessage().toString());
                        //Log.d("Check","Value : "+listlast);

                    }
                    else {
                        Log.d("Check","Value Empty");

                    }



                }

                Log.d("Check","On Data Value : "+listlast.get(listlast.size()-1));

                holder.tvDesc.setText(listlast.get(listlast.size()-1).toString());

                listlast.clear();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/



        // for image we need library ...
        if (chatlist.getUrlProfile().equals("")){
            //holder.profile.setImageResource(R.drawable.icon_male);  // set  default image when profile user is null

            Glide.with(context).load(R.drawable.icon_male).into(holder.profile);
        } else {
            Glide.with(context).load(chatlist.getUrlProfile()).into(holder.profile);
        }


        Log.d("Check","Check :"+chatlist.getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID",chatlist.getUserID())
                        .putExtra("userName",chatlist.getUserName())
                        .putExtra("userProfile",chatlist.getUrlProfile())
                        .putExtra("userPhone",chatlist.getPhone())
                        .putExtra("desc",chatlist.getDescription())

                );
            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogViewUser(context,chatlist);
            }
        });
    }

    public void lastmessage(final String userID, final TextView tvDesc) {

        theLastMessage = "default";
        messageRef.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    LastChats chats = dataSnapshot.getValue(LastChats.class);
                    Log.d("Check","CurrentUser : "+mCurrentUser);

                    Log.d("Check","CurrentUser : "+userID);
                    if (chats != null && chats.getSender().equals(mCurrentUser) && chats.getReceiver().equals(userID)
                            || chats.getReceiver().equals(mCurrentUser) && chats.getSender().equals(userID))
                    {
                        theLastMessage = chats.getLastMessage();
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        tvDesc.setText("No Message");
                        break;

                    default:
                        if(theLastMessage.length()>10)
                        {
                            tvDesc.setText("Text");

                        }
                        else
                        {
                            tvDesc.setText(theLastMessage);

                        }

                        break;
                }

                theLastMessage = "default";



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDesc, tvDate;
        private ImageView btnStatus;
        private CircularImageView profile;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvName = itemView.findViewById(R.id.tv_name);
            profile = itemView.findViewById(R.id.image_profile);
            btnStatus= itemView.findViewById(R.id.btn_status);
        }
    }
}
