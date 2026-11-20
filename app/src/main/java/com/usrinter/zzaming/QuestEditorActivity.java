package com.usrinter.zzaming;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

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

        String questId = getIntent().getStringExtra("questId");
        binding.questEditorTitleInput.setText(getIntent().getStringExtra("questTitle"));
        binding.questEditorDescriptionInput.setText(getIntent().getStringExtra("questDescription"));
        binding.questEditorRewardXpInput.setText(String.format(Locale.getDefault(), "%d", getIntent().getIntExtra("questXp", 0)));
        binding.questEditorBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
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

        updatePreview(binding);

        TextWatcher previewWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                updatePreview(binding);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        };

        binding.questEditorTitleInput.addTextChangedListener(previewWatcher);
        binding.questEditorRewardXpInput.addTextChangedListener(previewWatcher);

        binding.questEditorSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changedTitle = String.valueOf(binding.questEditorTitleInput.getText());
                String changedDescription = String.valueOf(binding.questEditorDescriptionInput.getText());
                String changedXpText = String.valueOf(binding.questEditorRewardXpInput.getText());
                int changedXpInt = 0;
                if(!(changedXpText.isEmpty())) {
                    try {
                        changedXpInt = Integer.parseInt(changedXpText);
                    } catch (NumberFormatException e) {
                        changedXpInt = 0;
                    }
                }
                if (changedXpInt < 1) {
                    binding.questEditorRewardXpInput.setError("XP는 1 이상 2147483647 이하로 입력해 주세요.");
                    binding.questEditorRewardXpInput.requestFocus();
                    return;
                }
                int changedCategoryId = binding.questEditorCategoryGroup.getCheckedRadioButtonId();
                QuestCategory changedCategory;

                if(changedCategoryId == R.id.quest_editor_category_life)
                    changedCategory = QuestCategory.LIFE;
                else if(changedCategoryId == R.id.quest_editor_category_admin)
                    changedCategory = QuestCategory.ADMIN;
                else if(changedCategoryId == R.id.quest_editor_category_duty)
                    changedCategory = QuestCategory.DUTY;
                else if(changedCategoryId == R.id.quest_editor_category_other)
                    changedCategory = QuestCategory.OTHER;
                else
                    changedCategory = null;

                setResult(RESULT_OK, new Intent()
                        .putExtra("questId", questId)
                        .putExtra("questTitle", changedTitle)
                        .putExtra("questDescription", changedDescription)
                        .putExtra("questXp", changedXpInt)
                        .putExtra("questCategory", changedCategory != null ? changedCategory.name() : null)
                );
                finish();
            }
        });
    }
    private void updatePreview(ActivityQuestEditorBinding binding) {
        String previewXp = String.valueOf(binding.questEditorRewardXpInput.getText());
        binding.questEditorPreviewTitle.setText(binding.questEditorTitleInput.getText());
        if(!(previewXp.isEmpty()))
            binding.questEditorPreviewDetails.setText(getString(R.string.quest_editor_preview_done, previewXp));
        else
            binding.questEditorPreviewDetails.setText(getString(R.string.quest_status_done));

    }
}