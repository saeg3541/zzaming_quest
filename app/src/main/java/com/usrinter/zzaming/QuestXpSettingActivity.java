package com.usrinter.zzaming;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.usrinter.zzaming.databinding.ActivityQuestXpSettingBinding;
import com.usrinter.zzaming.databinding.ItemXpLevelRuleBinding;
import com.usrinter.zzaming.databinding.ItemXpSettingRuleBinding;


import java.util.ArrayList;

public class QuestXpSettingActivity extends AppCompatActivity {
    private ActivityQuestXpSettingBinding binding;
    private final ArrayList<EditText> xpInputs = new ArrayList<>();
    private long totalQuestRewardXp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        //setContentView(R.layout.activity_quest_xp_setting);

        binding = ActivityQuestXpSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayList<Integer> levelRequirements = getIntent().getIntegerArrayListExtra("levelRequirements");
        totalQuestRewardXp = getIntent().getLongExtra("totalQuestRewardXp", -1L);


        TextWatcher xpWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshRuleSummary();
            }
        };

        // 회면 회전 반영
        ArrayList<String> restoredDrafts = savedInstanceState == null ? null : savedInstanceState.getStringArrayList("xpRuleDrafts");

        // 화면 회전시
        if (restoredDrafts != null && !restoredDrafts.isEmpty()) {
            for (String text : restoredDrafts) {
                addRuleRow(text, xpWatcher);
            }
            // 메인 화면에서 온 경우
        } else if (levelRequirements != null && !levelRequirements.isEmpty()) {
            for (int requiredXp : levelRequirements) {
                addRuleRow(String.valueOf(requiredXp), xpWatcher);
            }
            // 예외 상황(그냥 생짜 단일로 실행, 아무것도 없을시)
        } else {
            int[] defaults = {
                    R.string.quest_xp_default_1,
                    R.string.quest_xp_default_2,
                    R.string.quest_xp_default_3,
                    R.string.quest_xp_default_4,
                    R.string.quest_xp_default_5
            };

            for (int defaultText : defaults) {
                addRuleRow(getString(defaultText), xpWatcher);
            }
        }

        refreshRuleSummary();


        binding.xpSettingSave.setOnClickListener(view -> {
            ArrayList<Integer> changedRequirements = new ArrayList<>();

            for (EditText input : xpInputs) {
                String text = input.getText().toString().trim();

                if (text.isEmpty()) {
                    input.setError("필요 XP를 입력해 주세요.");
                    input.requestFocus();
                    return;
                }

                int requiredXp;
                try {
                    requiredXp = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    input.setError("2,147,483,647 이하의 정수를 입력해 주세요.");
                    input.requestFocus();
                    return;
                }

                if (requiredXp < 1) {
                    input.setError("필요 XP는 1 이상이어야 해요.");
                    input.requestFocus();
                    return;
                }

                changedRequirements.add(requiredXp);
            }

            setResult(RESULT_OK, new Intent()
                    .putIntegerArrayListExtra("levelRequirements", changedRequirements)
            );
            finish();
        });

        // 백 버튼 눌렀을시
        binding.xpSettingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


        // 구간 추가 버튼 리스너
        binding.xpSettingAddRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRuleRow("", xpWatcher);
                refreshRuleSummary();
                xpInputs.get(xpInputs.size() - 1).requestFocus();
            }
        });

        // 구간삭제 버튼
        binding.xpSettingRemoveRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastIndex = xpInputs.size() - 1;

                xpInputs.remove(lastIndex);
                binding.xpSettingRules.removeViewAt(lastIndex);
                refreshRuleSummary();
            }
        });
    }

    private ArrayList<Integer> readDraftRequirements(ArrayList<EditText> xpInputs) {
        ArrayList<Integer> requirements = new ArrayList<>();

        for (EditText input : xpInputs) {
            String text = input.getText().toString().trim();

            if (text.isEmpty()) {
                return null;
            }

            try {
                int xp = Integer.parseInt(text);
                if (xp < 1) {
                    return null;
                }
                requirements.add(xp);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return requirements;
    }

    private void bindXpSummary(ActivityQuestXpSettingBinding binding, ArrayList<Integer> levelRequirements, long totalQuestRewardXp) {
        if (totalQuestRewardXp < 0) {
            binding.xpSettingSummaryValue.setText(R.string.quest_xp_summary_placeholder);
            binding.xpSettingSummaryHint.setText("");
            return;
        }

        if (levelRequirements == null || levelRequirements.isEmpty()) {
            String summary = String.format(java.util.Locale.getDefault(), "— / %,d XP", totalQuestRewardXp);
            binding.xpSettingSummaryValue.setText(summary);
            binding.xpSettingSummaryHint.setText("");
            return;
        }

        long requiredXp = 0L;
        for (int xp : levelRequirements) {
            requiredXp += xp;
        }
        binding.xpSettingSummaryValue.setText(getString(R.string.quest_xp_summary_value, requiredXp, totalQuestRewardXp));

        long difference = requiredXp - totalQuestRewardXp;
        if (difference > 0) {
            binding.xpSettingSummaryHint.setText(getString(R.string.quest_xp_summary_shortage, difference));
        } else if (difference < 0) {
            binding.xpSettingSummaryHint.setText(getString(R.string.quest_xp_summary_surplus, -difference));
        } else {
            binding.xpSettingSummaryHint.setText(R.string.quest_xp_summary_equal);
        }
    }

    // 레벨 구간 뷰 하나 인플레이트, 뷰추가
    private void addRuleRow(String initialText, TextWatcher xpWatcher) {
        int fromLevel = xpInputs.size() + 1;

        ItemXpSettingRuleBinding row = ItemXpSettingRuleBinding.inflate(getLayoutInflater(), binding.xpSettingRules, false);

        row.ruleRange.setText(getString(R.string.quest_xp_detail_level_range, fromLevel, fromLevel + 1));

        row.ruleInput.setId(View.generateViewId());
        row.ruleRange.setLabelFor(row.ruleInput.getId());
        row.ruleInput.setText(initialText);

        xpInputs.add(row.ruleInput);
        binding.xpSettingRules.addView(row.getRoot());
        row.ruleInput.addTextChangedListener(xpWatcher);
    }


    // 입력값, 행 추가, 삭제시 필요XP 등 값들 갱신
    private void refreshRuleSummary() {
        bindXpSummary(binding, readDraftRequirements(xpInputs), totalQuestRewardXp);
        binding.xpSettingRemoveRange.setEnabled(xpInputs.size() > 1);
    }

    // 입력했던 문자열 저장
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> drafts = new ArrayList<>();
        for (EditText input : xpInputs) {
            drafts.add(input.getText().toString());
        }

        outState.putStringArrayList("xpRuleDrafts", drafts);
    }
}
