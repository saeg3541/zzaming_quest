package com.usrinter.zzaming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "LGG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button testSignOutButton = findViewById(R.id.test_logout_button);
        testSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                finish();
            }
        });



    }
    @Override
    public void onStart() {
        super.onStart();
        TextView test_sign_intent_text = findViewById(R.id.test_intent_text);
        Intent intent = getIntent();
        String userProfile = intent.getStringExtra("USER_PROFILE");
        test_sign_intent_text.setText(userProfile);
    }

    private void signOut() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        if (auth.getCurrentUser() == null) {
            Log.d(TAG, "signOut: Successful");
        }
        else {
            Log.d(TAG, "signOut: Failure");
        }
    }
}