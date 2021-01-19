package developermk.chatapp.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.developermk.chatapp.R;
import com.developermk.chatapp.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import developermk.chatapp.common.Common;
import developermk.chatapp.view.activities.display.ViewImageActivity;
import developermk.chatapp.view.activities.starup.SplashScreenActivity;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore,firestoreSignout;
    private DatabaseReference userRef,signoutRef;

    private BottomSheetDialog bottomSheetDialog, bsDialogEditName,dialogEdDesc;
    private ProgressDialog progressDialog;

    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_profile,container,false);
        View view = binding.getRoot();

        //View view =  inflater.inflate(R.layout.activity_profile, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        signoutRef=FirebaseDatabase.getInstance().getReference();
        firestoreSignout=FirebaseFirestore.getInstance();



        progressDialog = new ProgressDialog(view.getContext());

        if (firebaseUser!=null){
            getInfo();
        }

        initActionClick();

        return view;
    }


    private void initActionClick() {
        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPickPhoto();
            }
        });

        binding.lnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetEditName();
            }
        });
        binding.lnEditDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetEditDesc();
            }
        });
        binding.lnEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"This feature is not available",Toast.LENGTH_LONG).show();
            }
        });




        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imageProfile.invalidate();
                Drawable dr = binding.imageProfile.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), binding.imageProfile, "image");
                Intent intent = new Intent(getView().getContext(), ViewImageActivity.class);
                startActivity(intent, activityOptionsCompat.toBundle());


            }
        });

        binding.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSignOut();
            }
        });

    }

    private void showBottomSheetPickPhoto() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });
        ((View) view.findViewById(R.id.ln_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getView().getContext(),"Camera",Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(view.getContext());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog=null;
            }
        });

        bottomSheetDialog.show();
    }

    private void showBottomSheetEditName(){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);

        ((View) view.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsDialogEditName.dismiss();
            }
        });

        final EditText edUserName = view.findViewById(R.id.ed_username);
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String edUsername = documentSnapshot.getString("userName");
                edUserName.setText(edUsername);

            }
        });


        ((View) view.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edUserName.getText().toString())){
                    Toast.makeText(view.getContext(),"Name can'e be empty",Toast.LENGTH_SHORT).show();
                } else {
                    updateName(edUserName.getText().toString());
                    bsDialogEditName.dismiss();
                }
            }
        });

        bsDialogEditName = new BottomSheetDialog(view.getContext());
        bsDialogEditName.setContentView(view);
        bsDialogEditName.setCancelable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bsDialogEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bsDialogEditName = null;
            }
        });

        bsDialogEditName.show();
    }

    private void showBottomSheetEditDesc()
    {


        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_editdesc,null);

        final EditText edDesc=view.findViewById(R.id.ed_desc);

        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String edUsername = documentSnapshot.getString("bio");
                edDesc.setText(edUsername);
            }
        });

        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edDesc.getText().toString()))
                {
                    Toast.makeText(view.getContext(),"Description can't be empty",Toast.LENGTH_LONG).show();

                }
                else {

                    updateDesc(edDesc.getText().toString());
                    dialogEdDesc.dismiss();

                }

            }

            private void updateDesc(String desc) {

                HashMap<String,Object> map = new HashMap<>();
                map.put("description",desc);
                userRef.updateChildren(map);


                firestore.collection("Users").document(firebaseUser.getUid()).update("bio",desc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getInfo();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getView().getContext(),"Error"+e,Toast.LENGTH_LONG).show();

                    }
                });




            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialogEdDesc.dismiss();
            }
        });


        dialogEdDesc =new BottomSheetDialog(view.getContext());
        dialogEdDesc.setContentView(view);
        dialogEdDesc.setCancelable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialogEdDesc.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        dialogEdDesc.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogEdDesc =null;

            }
        });
        dialogEdDesc.show();




    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName = documentSnapshot.getString("userName");
                String userPhone = documentSnapshot.getString("userPhone");
                String imageProfile = documentSnapshot.getString("imageProfile");
                String desc = documentSnapshot.getString("bio");

                if (desc!=null) {
                    if (!desc.equals("")){
                        binding.tvAbout.setText(desc);

                    }                 }
                if ((imageProfile!=null))
                {
                    if (imageProfile.equals("")){
                        binding.imageProfile.setImageResource(R.drawable.icon_male);  // set  default image when profile user is null

                    } else {
                         Glide.with(getContext()).load(imageProfile).into( binding.imageProfile);

                    }

                }

                binding.tvUsername.setText(userName);
                binding.tvPhone.setText(userPhone);
                //binding.tvAbout.setText(desc);
               // Glide.with(getContext()).load(imageProfile).into(binding.imageProfile);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void openGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            imageUri = data.getData();

            uploadToFirebase();
            // try {
            //     Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            //     binding.imageProfile.setImageBitmap(bitmap);
//
            // }catch (Exception e){
            //     e.printStackTrace();
            // }

        }
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadToFirebase() {
        if (imageUri!=null){
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImagesProfile/" + System.currentTimeMillis()+"."+getFileExtention(imageUri));
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    final String sdownload_url = String.valueOf(downloadUrl);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageProfile", sdownload_url);

                    progressDialog.dismiss();
                   //Reference Storing
                    userRef.updateChildren(hashMap);
                    //Firestore Storing
                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getView().getContext(),"upload successfully",Toast.LENGTH_SHORT).show();

                                    getInfo();
                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getView().getContext(),"upload Failed",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void updateName(String newName){

        HashMap<String,Object> map = new HashMap<>();
        map.put("userName",newName);
        userRef.updateChildren(map);
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getView().getContext(),"Update Successful",Toast.LENGTH_SHORT).show();
                getInfo();
            }
        });
    }

    private void showDialogSignOut(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setMessage("Do you want to sign out?");
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
                Date date = new Date();
                String sentDate = dateFormat.format(date);



                HashMap<String,Object> onlineStateHashMap = new HashMap<>();
                onlineStateHashMap.put("datetime",sentDate);
                onlineStateHashMap.put("state","offline");

                signoutRef.child("Users").child(firebaseUser.getUid()).child("userState").updateChildren(onlineStateHashMap);
                firestoreSignout.collection("Users").document(firebaseUser.getUid()).update(onlineStateHashMap);



                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), SplashScreenActivity.class));
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
    }
}
