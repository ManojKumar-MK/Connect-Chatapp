package developermk.chatapp.view.activities.starup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.developermk.chatapp.R;
import developermk.chatapp.view.activities.auth.PhoneLoginActivity;

public class WelcomeScreenActivity extends AppCompatActivity {
    boolean doubleBackPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button btnAgree = findViewById(R.id.btn_agree);

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeScreenActivity.this, PhoneLoginActivity.class));
                finish();
            }
        });
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
