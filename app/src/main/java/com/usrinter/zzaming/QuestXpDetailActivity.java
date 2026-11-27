package com.usrinter.zzaming;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;


import com.usrinter.zzaming.databinding.ActivityQuestXpDetailBinding;
import com.usrinter.zzaming.databinding.ItemXpLevelRuleBinding;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class QuestXpDetailActivity extends AppCompatActivity {
    ArrayList<Integer> levelRequirements;
    private ActivityQuestXpDetailBinding binding;
    private long totalXp;
    private boolean requirementsChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        //setContentView(R.layout.activity_quest_xp_detail);

        binding = ActivityQuestXpDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActivityResultLauncher<Intent> xpSettingLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                Intent data = o.getData();
                if (o.getResultCode() != RESULT_OK || data == null) {
                    return;
                }

                ArrayList<Integer> changed = data.getIntegerArrayListExtra("levelRequirements");
                if (changed == null || changed.isEmpty()) {
                    return;
                }

                applyChangedRequirements(changed);

            }
        });



        boolean canEditQuest = getIntent().getBooleanExtra("canEditQuest", false);
        totalXp = getIntent().getLongExtra("totalXp", 0L);
        long totalQuestRewardXp = getIntent().getLongExtra("totalQuestRewardXp", -1L);
        levelRequirements = getIntent().getIntegerArrayListExtra("levelRequirements");
        if(levelRequirements == null)
            levelRequirements = new ArrayList<>();


        // 화면 회전시 대응
        ArrayList<Integer> restored = savedInstanceState == null ? null : savedInstanceState.getIntegerArrayList("changedLevelRequirements");

        if (restored != null) {
            applyChangedRequirements(restored);
        } else {
            updateXpDetail();
        }



        // 편집 권한에 따라 xp 세팅 보이게 할지 세팅
        if(canEditQuest) {
            binding.xpDetailOpenSettings.setVisibility(View.VISIBLE);
            binding.xpDetailOpenSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    xpSettingLauncher.launch(new Intent(QuestXpDetailActivity.this, QuestXpSettingActivity.class)
                            .putIntegerArrayListExtra("levelRequirements", levelRequirements)
                            .putExtra("totalQuestRewardXp", totalQuestRewardXp));
                }
            });
        } else {
            binding.xpDetailOpenSettings.setVisibility(View.GONE);
        }


        binding.xpDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    // 변경값 미리 결과 Intent 번들에 저장
    private void applyChangedRequirements(ArrayList<Integer> changed) {
        levelRequirements = changed;
        requirementsChanged = true;
        updateXpDetail();


        setResult(RESULT_OK, new Intent()
                .putIntegerArrayListExtra("levelRequirements", levelRequirements));
    }

    // 변경 값 있다면 저장해둠
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (requirementsChanged) {
            outState.putIntegerArrayList("changedLevelRequirements", levelRequirements);
        }
    }

    //xp 레벨업 기준들 보여주는 함수 (addView 활용)
    private void bindLevelRules() {
        binding.xpDetailRuleList.removeAllViews();

        for (int i = 0; i < levelRequirements.size(); i++) {
            ItemXpLevelRuleBinding row = ItemXpLevelRuleBinding.inflate(getLayoutInflater(), binding.xpDetailRuleList, false);

            int fromLevel = i + 1;
            int toLevel = i + 2;
            int requiredXp = levelRequirements.get(i);

            row.ruleRange.setText(
                    getString(R.string.quest_xp_detail_level_range, fromLevel, toLevel)
            );
            row.ruleXp.setText(
                    getString(R.string.quest_xp_rule_required_xp_format, requiredXp)
            );

            binding.xpDetailRuleList.addView(row.getRoot());
        }
    }

    // UI 업데이트 함수
    private void updateXpDetail() {
        long currentXp = totalXp;
        int level = 1;
        long needXp = 0;
        int nextLevelXp = 0;
        boolean isMaxLevel = true;
        for(int levelRequirement : levelRequirements) {
            // 현재 레벨 도달시에 (누적xp에서 필요xp를 계속 빼는데, 빼는 값이 필요 xp보다 작아질 경우)
            if(levelRequirement > currentXp) {
                nextLevelXp = levelRequirement;
                needXp = levelRequirement - currentXp;
                isMaxLevel = false;
                break;
            }
            currentXp -= levelRequirement;
            level++;
        }

        // 만랩 달성시
        if(isMaxLevel && !levelRequirements.isEmpty()) {
            nextLevelXp = levelRequirements.get(levelRequirements.size() - 1);
            currentXp += nextLevelXp;

            binding.xpDetailLevel.setText(R.string.quest_xp_detail_max_level);
            binding.xpDetailCurrentXp.setText(getString(R.string.quest_xp_detail_current_xp_format, currentXp, nextLevelXp));
            binding.xpDetailTotal.setText(getString(R.string.quest_xp_detail_xp_format, totalXp));

            binding.xpDetailRemaining.setText(R.string.quest_xp_detail_max_level_description);
        } else {
            binding.xpDetailLevel.setText(getString(R.string.quest_xp_current_level_format, level));
            binding.xpDetailCurrentXp.setText(getString(R.string.quest_xp_detail_current_xp_format, currentXp, nextLevelXp));
            binding.xpDetailTotal.setText(getString(R.string.quest_xp_detail_xp_format, totalXp));

            binding.xpDetailRemaining.setText(getString(R.string.quest_xp_detail_need_xp_format, level + 1, needXp));

        }


        // 진행 바 세팅
        binding.xpDetailProgress.setMax(nextLevelXp);
        binding.xpDetailProgress.setProgressCompat((int)currentXp, true);

        bindLevelRules();
    }

}
