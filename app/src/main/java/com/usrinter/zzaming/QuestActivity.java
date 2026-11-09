package com.usrinter.zzaming;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.usrinter.zzaming.databinding.ActivityQuestBinding;
import com.usrinter.zzaming.databinding.ItemQuestBinding;

import java.util.ArrayList;
import java.util.List;

public class QuestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityQuestBinding binding = ActivityQuestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Quest> quests = new ArrayList<>();
        quests.add(new Quest("testId001", "testBundleId", "취사장 마감 점검", "testDescription", 30));
        quests.add(new Quest("testId002", "testBundleId", "취사장 기구 정리", "testDescription", 20));
        quests.add(new Quest("testId003", "testBundleId", "취사장 뒷정리", "testDescription", 50));

        bindQuest(binding.questLockerRow, quests.get(0));
        bindQuest(binding.questLaundryRow, quests.get(1));
        bindQuest(binding.questLeaveRow, quests.get(2));
    }

    private void bindQuest(ItemQuestBinding row, Quest quest) {

        TextView questTitle = row.questItemTitle;
        questTitle.setText(quest.getTitle());

        TextView questDescription = row.questItemDetails;
        questDescription.setText(getString(R.string.quest_item_reward_format, quest.getDescription(), quest.getRewardXp()));


    }
}
