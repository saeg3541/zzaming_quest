package com.usrinter.zzaming;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.usrinter.zzaming.databinding.ActivityQuestEditorBinding;

import java.util.Locale;

public class QuestEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());

        ActivityQuestEditorBinding binding = ActivityQuestEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_quest_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.questEditorTitleInput.setText(getIntent().getStringExtra("questTitle"));
        binding.questEditorDescriptionInput.setText(getIntent().getStringExtra("questDescription"));
        binding.questEditorRewardXpInput.setText(String.format(Locale.getDefault(), "%d", getIntent().getIntExtra("questXp", 0)));
        switch (getIntent().getStringExtra("questCategory")) {
            case "LIFE":
                binding.questEditorCategoryGroup.check(R.id.quest_editor_category_life);
                break;
            case "DUTY":
                binding.questEditorCategoryGroup.check(R.id.quest_editor_category_duty);
                break;
            case "ADMIN":
                binding.questEditorCategoryGroup.check(R.id.quest_editor_category_admin);
                break;
            case "OTHER":
                binding.questEditorCategoryGroup.check(R.id.quest_editor_category_other);
                break;
            default:
                binding.questEditorCategoryGroup.check(R.id.quest_editor_category_life);
        }


    }
}