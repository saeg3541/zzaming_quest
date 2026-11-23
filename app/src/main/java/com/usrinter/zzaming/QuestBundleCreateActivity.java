package com.usrinter.zzaming;

import android.app.Activity;
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

import com.usrinter.zzaming.databinding.ActivityQuestBundleCreateBinding;

public class QuestBundleCreateActivity extends AppCompatActivity {
    private ActivityQuestBundleCreateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
//        setContentView(R.layout.activity_quest_bundle_create);
        binding = ActivityQuestBundleCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String rankText = getIntent().getStringExtra("rank");
        QuestRank rank = (rankText != null) ? QuestRank.valueOf(rankText) : QuestRank.PRIVATE;

        if(rank == QuestRank.PRIVATE) {
            binding.bundleCreateRank.setText(R.string.quest_rank_private);
        }
        else if(rank == QuestRank.FIRST_CLASS) {
            binding.bundleCreateRank.setText(R.string.quest_rank_first_class);
        }
        else if(rank == QuestRank.CORPORAL) {
            binding.bundleCreateRank.setText(R.string.quest_rank_corporal);
        }
        else if(rank == QuestRank.SERGEANT) {
            binding.bundleCreateRank.setText(R.string.quest_rank_sergeant);
        }

        binding.bundleCreateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


        TextWatcher previewWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                updatePreview();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        };

        binding.bundleCreateNameInput.addTextChangedListener(previewWatcher);
        binding.bundleCreateDescriptionInput.addTextChangedListener(previewWatcher);


        binding.bundleCreateSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bundleName = String.valueOf(binding.bundleCreateNameInput.getText()).trim();
                if (bundleName.isEmpty()) {
                    binding.bundleCreateNameInput.setError("묶음 이름을 입력해 주세요.");
                    binding.bundleCreateNameInput.requestFocus();
                    return;
                }
                String bundleDescription = String.valueOf(binding.bundleCreateDescriptionInput.getText());
                setResult(RESULT_OK, new Intent()
                        .putExtra("bundleName", bundleName)
                        .putExtra("bundleDescription", bundleDescription.trim())
                        .putExtra("rank", rank.name())
                );
                finish();
            }
        });
    }

    private void updatePreview() {
        String previewName = String.valueOf(binding.bundleCreateNameInput.getText()).trim();
        if (previewName.isEmpty()) {
            binding.bundleCreatePreviewName.setText(R.string.quest_bundle_preview_name);
        } else {
            binding.bundleCreatePreviewName.setText(previewName);
        }

        String previewDescription = String.valueOf(binding.bundleCreateDescriptionInput.getText()).trim();
        if (previewDescription.isEmpty()) {
            binding.bundleCreatePreviewDescription.setVisibility(View.GONE);
        } else {
            binding.bundleCreatePreviewDescription.setVisibility(View.VISIBLE);
            binding.bundleCreatePreviewDescription.setText(previewDescription);
        }
    }
}