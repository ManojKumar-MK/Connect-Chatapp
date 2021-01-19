package developermk.chatapp.view.activities.starup;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.developermk.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import developermk.chatapp.view.FirstActivity;

public class SplashScreenActivity extends AppCompatActivity {
    MediaPlayer mp;
    FirebaseUser firebaseUser;
    boolean doubleBackPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        mp= MediaPlayer.create(this,R.raw.hollow);
        mp.start();
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.animation);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1000);
        final ImageView splash = findViewById(R.id.icon);
        splash.startAnimation(animation);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, FirstActivity.class));
                    finish();
                }
            }, 3000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, WelcomeScreenActivity.class));
                    finish();
                }
            }, 3000);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

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
