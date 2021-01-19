package developermk.chatapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developermk.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import developermk.chatapp.managers.ChatService;
import developermk.chatapp.model.chat.Chats;
import developermk.chatapp.tools.AudioService;
import developermk.chatapp.tools.Tools;

public class ChatsAdapder extends RecyclerView.Adapter<ChatsAdapder.ViewHolder> {
    private List<Chats> list;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser firebaseUser;
    private ImageButton tmpBtnPlay;
    private AudioService audioService;
    private String receiverID;
    private ChatService chatService;
    DatabaseReference lastSeenRef=FirebaseDatabase.getInstance().getReference();

    private DatabaseReference LastRef= FirebaseDatabase.getInstance().getReference();
    public ChatsAdapder(List<Chats> list, Context context, String receiverID) {
        this.list = list;
        this.context = context;
        this.audioService = new AudioService(context);
        this.receiverID=receiverID;

    }

    public void setList(List<Chats> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_LEFT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view,1);

        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view, 0);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.bind(list.get(position),position);





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage,textDate,textSeen,imageDate,audioDate;
        private LinearLayout layoutText, layoutImage, layoutVoice;
        private LinearLayout layoutTextSeen,layoutImageSeen,layoutAudioSeen;
        private ImageView imageMessage,btnDeliverd,btnSeen;
        private CircularImageView imageView1,imageView2,imageView3;
        private ImageButton btnPlay;
        private ViewHolder tmpHolder;
        int viewSide;
        public ViewHolder(@NonNull View itemView, int i) {
            super(itemView);
          viewSide =i;


//            imageView1=itemView.findViewById(R.id.imageText);
//
//            imageView2=itemView.findViewById(R.id.imageImage);
//
//            imageView3=itemView.findViewById(R.id.imageRecord);



            textMessage = itemView.findViewById(R.id.tv_text_message);
            textDate=itemView.findViewById(R.id.tv_msg_date);

            layoutImage = itemView.findViewById(R.id.layout_image);
            layoutText = itemView.findViewById(R.id.layout_text);
            imageMessage = itemView.findViewById(R.id.image_chat);


            layoutVoice = itemView.findViewById(R.id.layout_voice);
            btnPlay = itemView.findViewById(R.id.btn_play_chat);
            textSeen=itemView.findViewById(R.id.tv_seen);

            layoutTextSeen = itemView.findViewById(R.id.ln_msg_seen);
            layoutImageSeen = itemView.findViewById(R.id.ln_image_seen);
            layoutAudioSeen = itemView.findViewById(R.id.ln_audio_seen);

            imageDate = itemView.findViewById(R.id.tv_image_date);
            audioDate = itemView.findViewById(R.id.tv_audio_date);

            btnDeliverd=itemView.findViewById(R.id.btn_delivered);
            btnSeen = itemView.findViewById(R.id.btn_seen);




        }
        @SuppressLint("ResourceType")
        void bind(final Chats chats,int position){
            //Check chat type..



            switch (chats.getType()){
                case "TEXT" :
                    // Glide.with(context).load(R.drawable.chat_icon).into(imageView1);
                    layoutText.setVisibility(View.VISIBLE);
                    layoutTextSeen.setVisibility(View.VISIBLE);

                    layoutImage.setVisibility(View.GONE);
                    layoutImageSeen.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);
                    layoutAudioSeen.setVisibility(View.GONE);



                    textMessage.setText(chats.getTextMessage());


                    String sentDate= Tools.messageSentDateProper(chats.getDateTime());

                    SpannableString dateString =new SpannableString(sentDate);
                    dateString.setSpan(new RelativeSizeSpan(0.7f), 0, sentDate.length(), 0);
                    // dateString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, sentDate.length(), 0);

                     Log.d("Check","Date : "+dateString);

                    textDate.setText(dateString);

                    if(position==list.size()-1)
                    {
                        if(chats.isIsseen())
                        {
                            textSeen.setText("Seen");
                            btnDeliverd.setVisibility(View.VISIBLE);

                            btnSeen.setVisibility(View.VISIBLE);

                            Log.d("Check","Seen ");
                        }

                        else {
                            btnDeliverd.setVisibility(View.VISIBLE);
                            btnSeen.setVisibility(View.GONE);

                            textSeen.setText("Delevered");

                        }
                    }
                    else {
                        textSeen.setVisibility(View.GONE);
                        btnDeliverd.setVisibility(View.GONE);
                        btnSeen.setVisibility(View.GONE);
                        Log.d("Check","Gone ");

                    }





                    break;
                case "IMAGE" :
                    // Glide.with(context).load(R.drawable.chat_icon).into(imageView2);
                    layoutText.setVisibility(View.GONE);
                    layoutTextSeen.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    layoutImageSeen.setVisibility(View.VISIBLE);
                    layoutVoice.setVisibility(View.GONE);
                    layoutAudioSeen.setVisibility(View.GONE);

                    String sentDateImage= Tools.messageSentDateProper(chats.getDateTime());

                    SpannableString dateStringImage =new SpannableString(sentDateImage);
                    dateStringImage.setSpan(new RelativeSizeSpan(0.7f), 0, sentDateImage.length(), 0);
                    // dateString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, sentDate.length(), 0);

                    // Log.d("Check","Date : "+dateString);

                    imageDate.setText(dateStringImage);

                    Glide.with(context).load(chats.getUrl()).into(imageMessage);

                    break;
                case "VOICE" :
                    //Glide.with(context).load(R.drawable.chat_icon).into(imageView3);
                    layoutText.setVisibility(View.GONE);
                    layoutTextSeen.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutImageSeen.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.VISIBLE);
                    layoutAudioSeen.setVisibility(View.VISIBLE);



                    layoutVoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tmpBtnPlay!=null){
                                tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                            }

                            btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_circle_filled_24));
                            audioService.playAudioFromUrl(chats.getUrl(), new AudioService.OnPlayCallBack() {
                                @Override
                                public void onFinished() {
                                    btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                                }
                            });

                            tmpBtnPlay = btnPlay;

                        }
                    });


                    String sentDateAudio= Tools.messageSentDateProper(chats.getDateTime());

                    SpannableString dateStringAudio =new SpannableString(sentDateAudio);
                    dateStringAudio.setSpan(new RelativeSizeSpan(0.7f), 0, sentDateAudio.length(), 0);
                    // dateString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, sentDate.length(), 0);

                    // Log.d("Check","Date : "+dateString);

                    audioDate.setText(dateStringAudio);


                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
         FirebaseUser firebaseUser;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (list.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else
        {

            return MSG_TYPE_LEFT;
        }
    }

}
