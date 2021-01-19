package developermk.chatapp.view.activities.chats;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.developermk.chatapp.R;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import developermk.chatapp.Notifications.APIService;
import developermk.chatapp.Notifications.Client;
import developermk.chatapp.Notifications.Data;
import developermk.chatapp.Notifications.MyResponse;
import developermk.chatapp.Notifications.Sender;
import developermk.chatapp.Notifications.Token;
import developermk.chatapp.adapter.ChatsAdapder;
import developermk.chatapp.interfaces.OnReadChatCallBack;
import developermk.chatapp.managers.ChatService;
import developermk.chatapp.managers.FirebaseImageLoader;
import developermk.chatapp.model.chat.Chats;
import developermk.chatapp.service.FirebaseService;
import developermk.chatapp.tools.Tools;
import developermk.chatapp.view.FirstActivity;
import developermk.chatapp.view.activities.dialog.DialogReviewSendImage;
import developermk.chatapp.view.activities.profile.UserProfileActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsActivity extends AppCompatActivity {

    private static final String TAG = "ChatsActivity";
    private static final int REQUEST_CORD_PERMISSION = 332;
   // private ActivityChatsBinding binding;
    private String receiverID,userPhone,desc;
    private ChatsAdapder adapder;
    private List<Chats>list = new ArrayList<>();
    private String userProfile,userName;
    private boolean isActionShown = false;
    private ChatService chatService;
    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;

    //Audio
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;

    private TextView tvUsername,tvStatus;
    private ImageView btnFile,btnEmoji,btnCamera;
    private EditText edMessage;
    private ImageView imageProfile;
    private FloatingActionButton btnSend;
    private RecordButton recordButton;
    private RecordView recordView;
    private RecyclerView recyclerView;
    private CardView layoutActions;
    private LinearLayout btnGallery,lnProfile;
    private ImageButton btnBack,btnDelete;
    APIService apiService ;


   private DatabaseReference chatRef1,chatRef2,rootRef;

    private DatabaseReference notifyRef;

    private DatabaseReference lastseenRef;
    private FirebaseFirestore firebaseFirestore;

    private DatabaseReference lastSeenRef = FirebaseDatabase.getInstance().getReference();


   private FirebaseUser user;

   boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //binding = DataBindingUtil.setContentView(this,R.layout.activity_chats);
        setContentView(R.layout.activity_chats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(ChatsActivity.this, FirstActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

         tvUsername=(TextView) findViewById(R.id.tv_username);
         imageProfile=(ImageView)findViewById(R.id.image_profile);
        btnFile=(ImageView)findViewById(R.id.btn_file);
         edMessage=(EditText) findViewById(R.id.ed_message);
        btnEmoji=(ImageView)findViewById(R.id.btn_emoji);
        btnCamera=(ImageView)findViewById(R.id.btn_camera);
        recordButton= (RecordButton) findViewById(R.id.record_button);
        recordView=(RecordView)findViewById(R.id.record_view);
        btnSend=(FloatingActionButton)findViewById(R.id.btn_send);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        layoutActions=(CardView)findViewById(R.id.layout_actions);
        btnGallery=(LinearLayout)findViewById(R.id.btn_gallery);
           lnProfile=(LinearLayout)findViewById(R.id.ln_top);
        btnDelete=(ImageButton)findViewById(R.id.btn_delete);
        tvStatus=(TextView)findViewById(R.id.tv_status);


        user=FirebaseAuth.getInstance().getCurrentUser();
        rootRef=FirebaseDatabase.getInstance().getReference();
        notifyRef=FirebaseDatabase.getInstance().getReference();

        firebaseFirestore=FirebaseFirestore.getInstance();
        lastseenRef = FirebaseDatabase.getInstance().getReference();

       apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        initialize();
        initBtnClick();

        readChats();
       // seenMessage(receiverID);


    }

    private void initialize(){

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        receiverID = intent.getStringExtra("userID");
        userProfile = intent.getStringExtra("userProfile");
        userPhone= intent.getStringExtra("userPhone");
        desc= intent.getStringExtra("desc");

        Log.d("Check","Check :"+userPhone);

        chatService = new ChatService(this,receiverID);

        if (receiverID!=null){
            Log.d(TAG, "onCreate: receiverID "+receiverID);

            if(userName==null)
            {
                rootRef.child("Users").child(receiverID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            if(snapshot.child("userState").hasChild("state")) {
                                String state = snapshot.child("userState").child("state").getValue().toString();
                                String datetime = snapshot.child("userState").child("datetime").getValue().toString();
                                Log.d(TAG,"state"+datetime);

                                if (state.equals("online")) {
                                    tvStatus.setText("Online");

                                }
                                if (state.equals("offline")) {
                                    String sentDate= Tools.messageSentDateProper(datetime);

                                    SpannableString dateString =new SpannableString(sentDate);
                                    dateString.setSpan(new RelativeSizeSpan(0.7f), 0, sentDate.length(), 0);

                                    Log.d(TAG,"state"+dateString);

                                    tvStatus.setText("Last seen " + dateString);
                                }
                            }

                            if(snapshot.hasChild("userProfile"))
                            {
                                String userProfile = snapshot.child("userProfile").getValue().toString();

//                                Glide.with(getApplicationContext())
//                                        .using(new com.firebase.ui.storage.images.FirebaseImageLoader()).load(userProfile).into(imageProfile);
//
                                Glide.with(getApplicationContext()).load(userProfile).apply(new RequestOptions()
                                        .apply(new FirebaseImageLoader())).into(imageProfile);



                            }
                            if(snapshot.hasChild("userName"))
                            {
                                userName = snapshot.child("userName").getValue().toString();
                                tvUsername.setText(userName);
                                desc = snapshot.child("description").getValue().toString();
                                userPhone = snapshot.child("userPhone").getValue().toString();

                            }
                            else {
                                tvStatus.setText("Offline");

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            else
            {
                rootRef.child("Users").child(receiverID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("userState").hasChild("state")) {
                            String state = snapshot.child("userState").child("state").getValue().toString();
                            String datetime = snapshot.child("userState").child("datetime").getValue().toString();
                            Log.d(TAG,"state"+datetime);

                            if (state.equals("online")) {
                                tvStatus.setText("Online");

                            }
                            if (state.equals("offline")) {
                                String sentDate= Tools.messageSentDateProper(datetime);

                                SpannableString dateString =new SpannableString(sentDate);
                                dateString.setSpan(new RelativeSizeSpan(0.7f), 0, sentDate.length(), 0);

                                Log.d(TAG,"state"+dateString);

                                tvStatus.setText("Last seen " + dateString);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                tvUsername.setText(userName);
                if (userProfile != null) {
                    if (userProfile.equals("")){
                        imageProfile.setImageResource(R.drawable.icon_male);  // set  default image when profile user is null
                    } else {
                        Glide.with(this).load(userProfile).into( imageProfile);
                    }
                }

            }








        }

        edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(edMessage.getText().toString())){
                    btnSend.setVisibility(View.INVISIBLE);
                    recordButton.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.VISIBLE);
                    recordButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //adapder = new ChatsAdapder(list,this);
        //binding.recyclerView.setAdapter(new ChatsAdapder(list,this));

        //initialize record button
        recordButton.setRecordView(recordView);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                //Start Recording..
                if (!checkPermissionFromDevice()) {
                    btnEmoji.setVisibility(View.INVISIBLE);
                    btnFile.setVisibility(View.INVISIBLE);
                    btnCamera.setVisibility(View.INVISIBLE);
                    edMessage.setVisibility(View.INVISIBLE);

                    startRecord();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(100);
                    }

                } else {
                    requestPermission();
                }

            }

            @Override
            public void onCancel() {
                try {
                    mediaRecorder.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime) {
                btnEmoji.setVisibility(View.VISIBLE);
                btnFile.setVisibility(View.VISIBLE);
                btnCamera.setVisibility(View.VISIBLE);
                edMessage.setVisibility(View.VISIBLE);

                //Stop Recording..
                try {
                    sTime = getHumanTimeText(recordTime);
                    stopRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLessThanSecond() {
                btnEmoji.setVisibility(View.VISIBLE);
                btnFile.setVisibility(View.VISIBLE);
                btnCamera.setVisibility(View.VISIBLE);
                edMessage.setVisibility(View.VISIBLE);
            }
        });
        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                btnEmoji.setVisibility(View.VISIBLE);
                btnFile.setVisibility(View.VISIBLE);
                btnCamera.setVisibility(View.VISIBLE);
                edMessage.setVisibility(View.VISIBLE);
            }
        });


    }



    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void readChats() {

        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chats> list) {


                recyclerView.setAdapter(new ChatsAdapder(list,ChatsActivity.this,receiverID));


            }

            @Override
            public void onReadFailed() {
                Log.d(TAG, "onReadFailed: ");
            }
        });
         seenMessage(receiverID);
    }

    public void seenMessage(final String receiverID) {

        lastSeenRef.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if (chats.getReceiver().equals(user.getUid()) && chats.getSender().equals(receiverID)){

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        Log.d("Check","onBind"+hashMap.toString());
                        dataSnapshot.getRef().updateChildren(hashMap);

                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initBtnClick(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;

                if (!TextUtils.isEmpty(edMessage.getText().toString())){
                    chatService.sendTextMsg(edMessage.getText().toString());
                    String msg = edMessage.getText().toString();

                        notification(msg);

                    edMessage.setText("");
                }
            }


        });

        final View view =findViewById(R.id.layout_actions);


//        final EmojIconActions emojIcon = new EmojIconActions(this,view , (EmojiconEditText) edMessage, btnEmoji, "#1c2764", "#e8e8e8", "#f4f4f4");
//        emojIcon.ShowEmojIcon();
//
//        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
//            @Override
//            public void onKeyboardOpen() {
//
//                view.setScrollContainer(true);
//
//
//                    }
//
//            @Override
//            public void onKeyboardClose() {
//
//            }
//        });



        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImageProfile();
            }
        });

        lnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImageProfile();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Delete Action",Toast.LENGTH_LONG).show();
              // showDialogDelete();



            }
        });





        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionShown){
                    layoutActions.setVisibility(View.GONE);
                    isActionShown = false;
                } else {
                    layoutActions.setVisibility(View.VISIBLE);
                    isActionShown = true;
                }

            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void notification(String msg) {

        final String message = msg;
        notifyRef.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild("userName"))
                {
                    if(notify)
                    {
                        String userName = snapshot.child("userName").getValue().toString();
                        Log.d("Check","onNotification :" +userName);

                        Log.d("Check","onNotification :" +receiverID);

                        Log.d("Check","onNotification :" +message);
                        sendNotification(receiverID,userName,message);



                    }
                    notify=false;


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void sendNotification(final String receiverID, final String userName, final String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Log.d("Check","onNotification"+user.getUid());

                    Log.d("Check","onNotification"+userName);

                    Log.d("Check","onNotification"+message);

                    Data data = new Data(user.getUid(), R.mipmap.ic_launcher, userName+": "+message, "New Message",
                            receiverID);
                    Log.d("Check","onNotification"+data.getSented());
                    Log.d("Check","onNotification"+data.getUser());
                    Log.d("Check","onNotification"+data.getBody());

                    Log.d("Check","onNotification"+token.getToken());

                    Sender sender = new Sender(data, token.getToken());

                    Log.d("Check","onNotification"+sender.getClass());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){

                                            Toast.makeText(ChatsActivity.this, "Failed!" , Toast.LENGTH_SHORT).show();
                                            Log.d("Check","Failed"+response.code());
                                            Log.d("Check","Failed"+response.message());

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

   /* private void showDialogDelete() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Delete all chat?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();


                chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(user.getUid()).child(receiverID).child("Messages");
                chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(user.getUid()).child("Messages");
                chatRef1.removeValue();
                chatRef2.removeValue();

                reference.child("Chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Chats chats = snapshot.getValue(Chats.class);
                            if (chats != null && chats.getSender().equals(user.getUid()) && chats.getReceiver().equals(receiverID)
                                    || chats.getReceiver().equals(user.getUid()) && chats.getSender().equals(receiverID)
                            ) {

                                reference.removeValue();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                 finish();


                //startActivity(new Intent(getApplicationContext(), FirstActivity.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/

    private void setImageProfile() {



        Log.d("Check","Check :"+userPhone);
        startActivity(new Intent(ChatsActivity.this, UserProfileActivity.class)
                .putExtra("userID",receiverID)
                .putExtra("userProfile",userProfile)
                .putExtra("userName",userName)
                .putExtra("userPhone",userPhone)
                .putExtra("desc",desc)

        );
    }

    private void openGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    private boolean checkPermissionFromDevice() {
        int write_external_strorage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_strorage_result == PackageManager.PERMISSION_DENIED || record_audio_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CORD_PERMISSION);
    }

    private void startRecord(){
        setUpMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            //  Toast.makeText(InChatActivity.this, "Recording...", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ChatsActivity.this, "Recording Error , Please restart your app ", Toast.LENGTH_LONG).show();
        }

    }

    private void stopRecord(){
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                //sendVoice();
                chatService.sendVoice(audio_path);

            } else {
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Stop Recording Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setUpMediaRecorder() {
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        } catch (Exception e) {
            Log.d(TAG, "setUpMediaRecord: " + e.getMessage());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            imageUri = data.getData();

            //uploadToFirebase();
             try {
                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                 reviewImage(bitmap);
             }catch (Exception e){
                 e.printStackTrace();
             }

        }
    }
    private void reviewImage(Bitmap bitmap){
        new DialogReviewSendImage(ChatsActivity.this,bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void onButtonSendClick() {
              // to Upload Image to firebase storage to get url image...
                if (imageUri!=null){
                    final ProgressDialog progressDialog = new ProgressDialog(ChatsActivity.this);
                    progressDialog.setMessage("Sending image...");
                    progressDialog.show();

                    //hide action buttonss
                    layoutActions.setVisibility(View.GONE);
                    isActionShown = false;

                    new FirebaseService(ChatsActivity.this).uploadImageToFireBaseStorage(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            // to send chat image//
                            chatService.sendImage(imageUrl);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                           e.printStackTrace();
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void UserStatus(String state)
    {

        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date date = new Date();
        String sentDate = dateFormat.format(date);



        HashMap<String,Object> onlineStateHashMap = new HashMap<>();
        onlineStateHashMap.put("datetime",sentDate);
        onlineStateHashMap.put("state",state);

        lastseenRef.child("Users").child(user.getUid()).child("userState").updateChildren(onlineStateHashMap);
        firebaseFirestore.collection("Users").document(user.getUid()).update(onlineStateHashMap);
    }
    @Override
    protected void onResume() {
        super.onResume();
        UserStatus("online");

    }

    @Override
    protected void onPause() {
        super.onPause();

        UserStatus("offline");

    }

    @Override
    protected void onStart() {
        super.onStart();
       UserStatus("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserStatus("offline");
    }
}
