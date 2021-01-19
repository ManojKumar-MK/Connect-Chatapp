package developermk.chatapp.view;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.developermk.chatapp.R;
import com.developermk.chatapp.databinding.UiMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import developermk.chatapp.animation.ViewAnimation;
import developermk.chatapp.menu.ChatsFragment;
import developermk.chatapp.menu.ContactsFragment;
import developermk.chatapp.menu.ProfileFragment;

public class FirstActivity extends AppCompatActivity {
    private UiMainBinding binding;
    MenuItem prevMenuItem;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private FirebaseFirestore firebaseFirestore;
    boolean doubleBackPressedOnce = false;
    boolean isRotate=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user=FirebaseAuth.getInstance().getCurrentUser();

        firebaseFirestore=FirebaseFirestore.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        //Usual setContentView(R.layout.ui_main)

        binding = DataBindingUtil.setContentView(this, R.layout.ui_main);

        setUpWithViewPager(binding.viewpager);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle("Chats");

        //Navigation Clicked

        binding.navigation.setItemSelected(R.id.chats,true);

        binding.navigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i)
                {
                    case R.id.chats:
                        binding.viewpager.setCurrentItem(0);
                        binding.toolbar.setTitle("Chats");
                        break;
                    case R.id.contacts:
                        binding.viewpager.setCurrentItem(1);
                        binding.toolbar.setTitle("Contacts");
                        fabTrigger();

                        break;
                    case R.id.profile:
                        binding.viewpager.setCurrentItem(2);
                        binding.toolbar.setTitle("Profile");
                        break;
                }
            }
        });

        // OnScrolling Towards Screen

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.d("Bottom Activity","Position " + position);

                switch (position)
                {
                    case 0:
                        binding.navigation.setItemSelected(R.id.chats,true);
                        binding.toolbar.setTitle("Chats");break;
                    case 1:
                        binding.navigation.setItemSelected(R.id.contacts,true);
                        binding.toolbar.setTitle("Contacts");fabTrigger();break;
                    case 2:
                        binding.navigation.setItemSelected(R.id.profile,true);
                        binding.toolbar.setTitle("Profile");break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void fabTrigger() {

        binding.fabAction.setVisibility(View.VISIBLE);
        ViewAnimation.init(binding.fabAdd);
        ViewAnimation.init(binding.fabSearch);
        binding.fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRotate=true;
                ViewAnimation.rotateFab(view,isRotate);
                if(isRotate)
                {
                    ViewAnimation.showIn(binding.fabAdd);
                    ViewAnimation.showIn(binding.fabSearch);

                }
                else {
                    ViewAnimation.showOut(binding.fabAdd);
                    ViewAnimation.showOut(binding.fabSearch);

                }

            }
        });
    }

    private void setUpWithViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter sectionPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionPageAdapter.addfragment(new ChatsFragment());
        sectionPageAdapter.addfragment(new ContactsFragment());
        sectionPageAdapter.addfragment(new ProfileFragment());
        viewPager.setAdapter(sectionPageAdapter);

    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public SectionsPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }



        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        public void addfragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    private void UserStatus(String state)
    {


        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date date = new Date();
        String sentDate = dateFormat.format(date);



        HashMap<String,Object> onlineStateHashMap = new HashMap<>();
        onlineStateHashMap.put("datetime",sentDate);
        onlineStateHashMap.put("state",state);

        rootRef.child("Users").child(user.getUid()).child("userState").updateChildren(onlineStateHashMap);
        firebaseFirestore.collection("Users").document(user.getUid()).update(onlineStateHashMap);
    }

    @Override
    protected void onStop() {

        super.onStop();

            UserStatus("offline");


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

            UserStatus("offline");


    }

    @Override
    protected void onStart() {

        super.onStart();

            UserStatus("online");
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
    public void onBackPressed() {
        if(doubleBackPressedOnce)
        {
            super.onBackPressed();
            return;
        }
        this.doubleBackPressedOnce=true;
        Toast.makeText(this, "Tab again back to exit... ", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackPressedOnce=false;
            }
        },2000);
    }
}
