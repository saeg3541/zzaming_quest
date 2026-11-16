package com.usrinter.zzaming;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.usrinter.zzaming.databinding.ActivityQuestBinding;
import com.usrinter.zzaming.databinding.ActivityQuestDetailBinding;

public class QuestDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());

        ActivityQuestDetailBinding binding = ActivityQuestDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setContentView(R.layout.activity_quest_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.questDetailTitle.setText(getIntent().getStringExtra("questTitle"));
        binding.questDetailDescription.setText(getIntent().getStringExtra("questDescription"));
        binding.questDetailRewardXp.setText(getString(R.string.quest_detail_xp_reward, getIntent().getIntExtra("questXp", 0)));

        binding.questDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}