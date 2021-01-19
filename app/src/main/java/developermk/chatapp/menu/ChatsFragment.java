package developermk.chatapp.menu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.developermk.chatapp.R;
import com.developermk.chatapp.databinding.FragmentChatsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import developermk.chatapp.Notifications.Token;
import developermk.chatapp.adapter.ChatListAdapter;
import developermk.chatapp.model.Chatlist;
import developermk.chatapp.view.activities.contact.ContactsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";

    public ChatsFragment() {
        // Required empty public constructor
    }

    private FirebaseUser firebaseUser;
    private DatabaseReference reference,chatRef,messageRef;
    private FirebaseFirestore firestore;
    private Handler handler = new Handler();

    private List<Chatlist> list;

    private FragmentChatsBinding binding;

    private ArrayList<String> allUserID;

    private ChatListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chats, container, false);

        list = new ArrayList<>();
        allUserID = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        chatRef=FirebaseDatabase.getInstance().getReference();

        messageRef = FirebaseDatabase.getInstance().getReference();

        firestore = FirebaseFirestore.getInstance();

        binding.lnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ContactsActivity.class));
            }
        });

        if (firebaseUser!=null) {
            getChatList();
        }

        updateToken(FirebaseInstanceId.getInstance().getToken());


        return binding.getRoot();
    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void getChatList() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        list.clear();
        allUserID.clear();;
        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                },2000);
                if(!dataSnapshot.exists())
                {
                 binding.lnInvite.setVisibility(View.VISIBLE);
                 binding.progressCircular.setVisibility(View.GONE);
                }
                else{

                    binding.lnInvite.setVisibility(View.INVISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String userID = Objects.requireNonNull(snapshot.child("chatid").getValue()).toString();
                        Log.d(TAG, "onDataChange: userid "+userID);

                        binding.progressCircular.setVisibility(View.GONE);
                        allUserID.add(userID);
                    }
                    adapter = new ChatListAdapter(list,getContext(),true);
                    binding.recyclerView.setAdapter(adapter);
                    getUserInfo();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void getUserInfo(){



        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : allUserID){
                    firestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAG, "onSuccess: ddd"+documentSnapshot.getString("userName"));
                            try {
                                Chatlist chat = new Chatlist(
                                        documentSnapshot.getString("userID"),
                                        documentSnapshot.getString("userName"),
                                        documentSnapshot.getString("bio"),
                                        documentSnapshot.getString("date"),
                                        documentSnapshot.getString("imageProfile"),
                                        documentSnapshot.getString("userPhone"),
                                        ""
                                );
                                list.add(chat);
                            }catch (Exception e){
                                Log.d(TAG, "onSuccess: "+e.getMessage());
                            }
                            //Adapter Set For ChatList

                            if (adapter!=null){
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();

                                Log.d(TAG, "onSuccess: adapter "+adapter.getItemCount());
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Error L"+e.getMessage());
                        }
                    });
                }
            }
        });
    }

}
