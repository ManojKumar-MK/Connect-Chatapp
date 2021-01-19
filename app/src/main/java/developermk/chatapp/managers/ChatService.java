package developermk.chatapp.managers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import developermk.chatapp.interfaces.OnReadChatCallBack;
import developermk.chatapp.model.Chatlist;
import developermk.chatapp.model.chat.Chats;

public class ChatService {
    private Context context;
    Chatlist chatlist;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private  DatabaseReference chatRef1=FirebaseDatabase.getInstance().getReference();
    private  DatabaseReference chatRef2=FirebaseDatabase.getInstance().getReference();
    private DatabaseReference messageRef1=FirebaseDatabase.getInstance().getReference();
    private DatabaseReference messageRef2=FirebaseDatabase.getInstance().getReference();

    private DatabaseReference lastSeenRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String receiverID;

    public ChatService(Context context, String receiverID) {
        this.context = context;
        this.receiverID = receiverID;
    }


    public void readChatData(final OnReadChatCallBack onCallBack){
        reference.child("Chats")

        //reference.child("ChatList").child(firebaseUser.getUid()).child(receiverID).child("Messages")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chats> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats != null && chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)
                            || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)
                    ) {
                        list.add(chats);


                        //list.lastIndexOf(chats);

                    }
                }


                onCallBack.onReadSuccess(list);
               // seenMessage(receiverID);


            }




                    @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onCallBack.onReadFailed();
            }
        });

    }
    public void seenMessage(final String receiverID) {

        lastSeenRef.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if (chats != null && chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)
                            || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)
                    ){

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
    public void sendTextMsg(String text){

        Chats chats = new Chats(
                getCurrentDate(),
                text,
                "",
                "TEXT",
                firebaseUser.getUid(),
                receiverID,
                text,
                false

        );



//        messageRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
//        messageRef1.child("Chats").push().setValue(chats);
//
//        messageRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
//        messageRef2.child("Chats").push().setValue(chats);


        //Add to ChatList
        chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        //
        chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());






        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Send", "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Send", "onFailure: "+e.getMessage());
            }
        });


        //Notification Ref Starting








    }

    public void sendImage(String imageUrl){

        Chats chats = new Chats(
                getCurrentDate(),
                "",
                imageUrl,
                "IMAGE",
                firebaseUser.getUid(),
                receiverID,
                "Image",
                false
        );

//        messageRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
//        messageRef1.child("Chats").push().setValue(chats);
//
//        messageRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
//        messageRef2.child("Chats").push().setValue(chats);

        reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Send", "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Send", "onFailure: "+e.getMessage());
            }
        });

        //Add to ChatList
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatRef1.child("chatid").setValue(receiverID);

        //
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatRef2.child("chatid").setValue(firebaseUser.getUid());
    }

    public String getCurrentDate(){
//        Date date = Calendar.getInstance().getTime();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        String today = formatter.format(date);
//
//        Calendar currentDateTime = Calendar.getInstance();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
//        String currentTime = df.format(currentDateTime.getTime());
//
//        return today+", "+currentTime;

        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date date = new Date();
        String sentDate = dateFormat.format(date);

        return sentDate;

    }

    public void sendVoice(String audioPath){
        final Uri uriAudio = Uri.fromFile(new File(audioPath));
        final StorageReference audioRef = FirebaseStorage.getInstance().getReference().child("Chats/Voice/" + System.currentTimeMillis());
        audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot audioSnapshot) {
                Task<Uri> urlTask = audioSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                String voiceUrl = String.valueOf(downloadUrl);

                Chats chats = new Chats(
                        getCurrentDate(),
                        "",
                        voiceUrl,
                        "VOICE",
                        firebaseUser.getUid(),
                        receiverID,
                        "AudioFile",
                        false
                );

//                messageRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
//                messageRef1.child("Chats").push().setValue(chats);
//
//                messageRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
//                messageRef2.child("Chats").push().setValue(chats);

                reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Send", "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Send", "onFailure: "+e.getMessage());
                    }
                });

                //Add to ChatList
                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
                chatRef1.child("chatid").setValue(receiverID);

                //
                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
                chatRef2.child("chatid").setValue(firebaseUser.getUid());
            }
        });
    }

}
