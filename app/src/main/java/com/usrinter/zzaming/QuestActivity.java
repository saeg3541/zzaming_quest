package com.usrinter.zzaming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.usrinter.zzaming.databinding.ActivityQuestBinding;
import com.usrinter.zzaming.databinding.ItemQuestBinding;

import java.util.ArrayList;
import java.util.List;

public class QuestActivity extends AppCompatActivity {

    //임시 퀘스트 진행 리스트
    private List<QuestProgress> questProgresses;

    //바인딩 다른 함수에서도 불러올 수 있게 필드에 선언
    private ActivityQuestBinding binding;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        //바인딩
        binding = ActivityQuestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //임시 퀘스트 번들 선언
        QuestBundle testQuestBundle = new QuestBundle("testBundleId001", "취사반 적응 미션", "testDescription");

        //퀘스트 묶음 이름, 설명 setText
        binding.questBundleName.setText(testQuestBundle.getName());
        binding.questBundleDescription.setText(testQuestBundle.getDescription());

        //임시 퀘스트 리스트 및 만들기
        List<Quest> quests = new ArrayList<>();
        quests.add(new Quest("testId001", testQuestBundle.getBundleId(), "취사장 마감 점검", "testDescription", 30, QuestCategory.LIFE));
        quests.add(new Quest("testId002", testQuestBundle.getBundleId(), "취사장 기구 정리", "testDescription", 20, QuestCategory.ADMIN));
        quests.add(new Quest("testId003", testQuestBundle.getBundleId(), "취사장 뒷정리", "testDescription", 50, QuestCategory.DUTY));
        quests.add(new Quest("testId004", testQuestBundle.getBundleId(), "취사장 뒷정리2", "testDescription", 50, QuestCategory.OTHER));

        //임시 퀘스트 진행 리스트
        questProgresses = new ArrayList<>();
        for(int i = 0; i < quests.size(); i++) {
            questProgresses.add(new QuestProgress(quests.get(i).getQuestId()));
        }

        // 퀘스트 완료/총개수 텍스트 업데이트
        updateQuestCount();


        QuestAdapter questAdapter = new QuestAdapter(quests, questProgresses, () -> {
            // 퀘스트 체크 건들이면 할일
                updateQuestCount();
            },(quest) -> {
            // 퀘스트 제목, >기호 클릭하면 할일
                clickQuest(quest);
            });
        // 리사이클러 뷰 레이아웃 메니저, 어뎁터 설정(뷰 홀더는 어뎁터쪽 클래스에 같이 선언)
        binding.questRowRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.questRowRecycler.setAdapter(questAdapter);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                Log.d("LEE", "onActivityResult() to launcher");
                if(o.getResultCode() == RESULT_OK) {
                    Quest selectedQuest = null;
                    for(Quest quest : quests) {
                        if (quest.getQuestId().equals(o.getData().getStringExtra("questId"))) {
                            selectedQuest = quest;
                            break;
                        }
                    }
                    if (selectedQuest != null) {
                        String categoryName = o.getData().getStringExtra("questCategory");
                        String title = o.getData().getStringExtra("questTitle");
                        String description = o.getData().getStringExtra("questDescription");
                        int rewardXp = o.getData().getIntExtra("questXp", 0);

                        if (categoryName != null)
                            selectedQuest.setCategory(QuestCategory.valueOf(categoryName));
                        if (title != null)
                            selectedQuest.setTitle(title);
                        if (description != null)
                            selectedQuest.setDescription(description);
                        if (rewardXp > 0)
                            selectedQuest.setRewardXp(rewardXp);
                    }
                    questAdapter.notifyDataSetChanged();
                }
            }
        });


    }

//    기존 리사이클러 뷰 적용 전 테스트 함수
//    private void bindQuest(ItemQuestBinding row, Quest quest, QuestProgress questProgress) {
//        TextView questTitle = row.questItemTitle;
//        questTitle.setText(quest.getTitle());
//
//        TextView questDescription = row.questItemDetails;
//        questDescription.setText(getString(R.string.quest_item_reward_format, quest.getDescription(), quest.getRewardXp()));
//
//
//        row.questItemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean isChecked) {
//                questProgress.setCompleted(isChecked);
//                updateQuestCount();
//            }
//        });
//    }

    private void updateQuestCount() {
        int totalQuestCount = questProgresses.size();
        int totalQuestCompleted = 0;
        for(QuestProgress questProgress : questProgresses) {
            if(questProgress.isCompleted())
                totalQuestCompleted++;
        }
        binding.questBundleProgress.setText(getString(R.string.quest_bundle_progress_format, totalQuestCompleted, totalQuestCount));
    }

    // 클릭시 할 일, 퀘스트를 받아서 인텐트 객체를 만든다음 extra값에 퀘스트 정보를 넣음
    // 여러 extra 넣는게 되네?
    // 그리고 퀘스트를 startActvity로 다른 액티비티 실행, 아마도 교수님께서 뭐였지? 그그그그
    // 아 매니저한테 인텐트 객체를 넘기는거라고 하셨네 그리고 매니저가 실행
    private void clickQuest(Quest quest) {
        boolean canEditQuest = true;
        if(canEditQuest == true) {
            launcher.launch(new Intent(QuestActivity.this, QuestEditorActivity.class)
                    .putExtra("questId", quest.getQuestId())
                    .putExtra("questCategory", quest.getCategory().name())
                    .putExtra("questTitle", quest.getTitle())
                    .putExtra("questDescription", quest.getDescription())
                    .putExtra("questXp", quest.getRewardXp())
            );
        } else {
            startActivity(new Intent(QuestActivity.this, QuestDetailActivity.class)
                    .putExtra("questTitle", quest.getTitle())
                    .putExtra("questDescription", quest.getDescription())
                    .putExtra("questXp", quest.getRewardXp())
            );
        }
    }
}
