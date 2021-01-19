package developermk.chatapp.adapter;

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

import java.util.List;

import developermk.chatapp.model.user.Users;
import developermk.chatapp.view.activities.chats.ChatsActivity;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<Users> list;
    private Context context;

    public ContactsAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users user = list.get(position);

        holder.username.setText(user.getUserName());
        holder.desc.setText(user.getBio());


        Log.d("Check","Contact : "+  user.getImageProfile());

        if (user.getImageProfile().equals("")){
            //holder.profile.setImageResource(R.drawable.icon_male);  // set  default image when profile user is null

            Glide.with(context).load(R.drawable.icon_male).into(holder.imageProfile);
        } else {
            Glide.with(context).load(user.getImageProfile()).into(holder.imageProfile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID",user.getUserID())
                        .putExtra("userName",user.getUserName())
                        .putExtra("userProfile",user.getImageProfile())
                        .putExtra("userPhone",user.getUserPhone())
                        .putExtra("desc",user.getBio())
                );
            }
        });


        holder.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnDeny.setVisibility(View.VISIBLE);

            }
        });

        holder.btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnDeny.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageProfile,btnRequest,btnDeny;
        private TextView username,desc;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.tv_username);
            desc = itemView.findViewById(R.id.tv_desc);
            btnRequest=itemView.findViewById(R.id.btn_request);
            btnDeny=itemView.findViewById(R.id.btn_deny);



        }
    }
}
