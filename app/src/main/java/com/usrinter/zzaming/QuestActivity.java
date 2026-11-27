package com.usrinter.zzaming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.lifecycle.ViewModelProvider;

import com.usrinter.zzaming.databinding.ActivityQuestBinding;
import com.usrinter.zzaming.databinding.ItemQuestBinding;
import com.usrinter.zzaming.databinding.ItemQuestBundleBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestActivity extends AppCompatActivity {
    boolean canEditQuest = true;
    private List<Quest> quests;
    private List<QuestBundle> questBundles;
    private QuestRank rank;

    //임시 퀘스트 진행 리스트
    private List<QuestProgress> questProgresses;

    //바인딩 및 런처 onCreate 말고 다른 함수에서도 불러올 수 있게 필드에 선언
    private ActivityQuestBinding binding;

    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<Intent> bundleCreateLauncher;
    private ActivityResultLauncher<Intent> bundleEditLauncher;
    private ActivityResultLauncher<Intent> xpLauncher;



    private QuestViewModel questViewModel;

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

        //편집 권한 확인(간부), 메인 엑티비티로 들어왔을 때 가정
        canEditQuest = getIntent().getBooleanExtra("canEditQuest", false);

        // 뷰 모델의 퀘스트, 진행사항, 묶음 가져오기(레퍼런스도 뷰모델꺼 사용)
        questViewModel = new ViewModelProvider(this).get(QuestViewModel.class);

        questBundles = questViewModel.getQuestBundles();
        quests = questViewModel.getQuests();
        questProgresses = questViewModel.getQuestProgresses();
        // 체크한 랭크 불러오기
        questViewModel.getSelectedRank().observe(this, new Observer<QuestRank>() {
            @Override
            public void onChanged(QuestRank questRank) {
                rank = questRank;
                switch (rank) {
                    case PRIVATE:
                        binding.questRankTabs.check(R.id.quest_rank_tab_private);
                        break;
                    case FIRST_CLASS:
                        binding.questRankTabs.check(R.id.quest_rank_tab_first_class);
                        break;
                    case CORPORAL:
                        binding.questRankTabs.check(R.id.quest_rank_tab_corporal);
                        break;
                    case SERGEANT:
                        binding.questRankTabs.check(R.id.quest_rank_tab_sergeant);
                        break;
                }
                showBundlesForRank(rank);
            }
        });




        // 기존 퀘스트 예시 생성
//        rank = QuestRank.FIRST_CLASS;
//
//        //임시 퀘스트 번들 선언
//        questBundles = new ArrayList<>();
//        questBundles.add(new QuestBundle("testBundleId001", "취사반 적응 미션", "testDescription", QuestRank.FIRST_CLASS));
//        questBundles.add(new QuestBundle("testBundleId002", "행정 업무 익히기", "testDescription2", QuestRank.FIRST_CLASS));
//        questBundles.add(new QuestBundle("testBundleId003", "전입 처음", "testDescription3", QuestRank.PRIVATE));
//
//
//        //퀘스트 묶음 이름, 설명 setText
//
//
//        //임시 퀘스트 리스트 및 만들기
//        quests = new ArrayList<>();
//        quests.add(new Quest("testId001", questBundles.get(0).getBundleId(), "취사장 마감 점검", "testDescription", 30, QuestCategory.LIFE));
//        quests.add(new Quest("testId002", questBundles.get(0).getBundleId(), "취사장 기구 정리", "testDescription", 20, QuestCategory.ADMIN));
//        quests.add(new Quest("testId003", questBundles.get(0).getBundleId(), "취사장 뒷정리", "testDescription", 50, QuestCategory.DUTY));
//        quests.add(new Quest("testId004", questBundles.get(0).getBundleId(), "취사장 뒷정리2", "testDescription", 50, QuestCategory.OTHER));
//
//        quests.add(new Quest("testId005", questBundles.get(1).getBundleId(), "취사장 마감 점검22", "testDescription22", 22, QuestCategory.LIFE));
//        quests.add(new Quest("testId006", questBundles.get(1).getBundleId(), "취사장 기구 정리22", "testDescription22", 20, QuestCategory.ADMIN));
//
//        quests.add(new Quest("testId007", questBundles.get(2).getBundleId(), "관물대 정리", "testDescription3", 30, QuestCategory.LIFE));
//        quests.add(new Quest("testId008", questBundles.get(2).getBundleId(), "침대 정리", "testDescription3", 30, QuestCategory.DUTY));
//        quests.add(new Quest("testId009", questBundles.get(2).getBundleId(), "책상 정리", "testDescription3", 30, QuestCategory.ADMIN));
//
//
//
//        //임시 퀘스트 진행 리스트
//        questProgresses = new ArrayList<>();
//        for(int i = 0; i < quests.size(); i++) {
//            questProgresses.add(new QuestProgress(quests.get(i).getQuestId()));
//        }

        // 퀘스트 완료/총개수 텍스트 업데이트


        //기존 묶음 1개 퀘스트 어뎁터
//        QuestAdapter questAdapter = new QuestAdapter(quests, questProgresses, () -> {
//            // 퀘스트 체크 건들이면 할일
//                updateQuestCount();
//            }
//            ,(quest) -> {
//            // 퀘스트 제목, >기호 클릭하면 할일
//                clickQuest(quest);
//            });
        // 리사이클러 뷰 레이아웃 메니저, 어뎁터 설정(뷰 홀더는 어뎁터쪽 클래스에 같이 선언)
//        binding.questRowRecycler.setLayoutManager(new LinearLayoutManager(this));
//        binding.questRowRecycler.setAdapter(questAdapter);

        // 퀘스트 편집, 생성 액티비티 런처 등록
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                Log.d("LEE", "onActivityResult() to launcher");

                if(o.getResultCode() == RESULT_OK && o.getData() != null) {
                    String categoryName = o.getData().getStringExtra("questCategory");
                    QuestCategory category = (categoryName != null) ? QuestCategory.valueOf(categoryName) : null;
                    String title = o.getData().getStringExtra("questTitle");
                    String description = o.getData().getStringExtra("questDescription");
                    int rewardXp = o.getData().getIntExtra("questXp", 0);

                    if(o.getData().getBooleanExtra("isCreateMode", false)) {
                        String bundleId = o.getData().getStringExtra("bundleId");
                        String newQuestId = UUID.randomUUID().toString();
                        Quest newQuest = new Quest(newQuestId, bundleId, title, description, rewardXp, category);
                        QuestProgress newQuestProgress = new QuestProgress(newQuestId);

                        quests.add(newQuest);
                        questProgresses.add(newQuestProgress);
                    }
                    else {
                        Quest selectedQuest = null;
                        for(Quest quest : quests) {
                            if (quest.getQuestId().equals(o.getData().getStringExtra("questId"))) {
                                selectedQuest = quest;
                                break;
                            }
                        }
                        if (selectedQuest != null) {
                            if (category != null)
                                selectedQuest.setCategory(category);
                            if (title != null)
                                selectedQuest.setTitle(title);
                            if (description != null)
                                selectedQuest.setDescription(description);
                            if (rewardXp > 0)
                                selectedQuest.setRewardXp(rewardXp);
                        }
                    }
                    showBundlesForRank(rank);
                }
            }
        });

        // 몪음 추가 버튼 누를시 런처 등록
        bundleCreateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                Intent data = o.getData();
                if(o.getResultCode() == RESULT_OK && data != null) {
                    String newBundleId = UUID.randomUUID().toString();
                    String newBundleName = data.getStringExtra("bundleName");
                    String newBundleDescription = data.getStringExtra("bundleDescription");
                    QuestRank newBundleRank = QuestRank.valueOf(data.getStringExtra("rank"));

                    QuestBundle newQuestBundle = new QuestBundle(newBundleId, newBundleName, newBundleDescription, newBundleRank);

                    questBundles.add(newQuestBundle);

                    showBundlesForRank(rank);
                }
            }
        });

        // 묶음 편집 버튼 누를시 런처 등록
        bundleEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                Intent data = o.getData();
                if((o.getResultCode() == RESULT_OK) && (data != null)) {
                    String changedBundleName = data.getStringExtra("bundleName");
                    String changedBundleId = data.getStringExtra("bundleId");
                    String changedBundleDescription = data.getStringExtra("bundleDescription");

                    QuestBundle selectedQuestBundle = null;
                    for(QuestBundle bundle : questBundles) {
                        if(bundle.getBundleId().equals(changedBundleId)) {
                            selectedQuestBundle = bundle;
                            break;
                        }
                    }



                    if(selectedQuestBundle != null) {
                        // 만약 번들 지우라는 값이 true라면 번들 및 퀘스트 삭제
                        if (data.getBooleanExtra("deleteBundle", false)) {
                            deleteQuestBundle(selectedQuestBundle);
                            showBundlesForRank(rank);
                            return;
                        }

                        if (changedBundleName != null)
                            selectedQuestBundle.setName(changedBundleName);
                        if (changedBundleDescription != null)
                            selectedQuestBundle.setDescription(changedBundleDescription);

                        ArrayList<String> deletedQuestIds = data.getStringArrayListExtra("deletedQuestIds");
                        if (deletedQuestIds != null && !deletedQuestIds.isEmpty()) {
                            removeQuestsFromBundle(selectedQuestBundle.getBundleId(), deletedQuestIds);
                        }

                        ArrayList<String> orderedQuestIds = data.getStringArrayListExtra("orderedQuestIds");

                        if (orderedQuestIds != null) {
                            applyQuestOrder(selectedQuestBundle.getBundleId(), orderedQuestIds);
                        }
                    }
                    showBundlesForRank(rank);
                }
            }
        });

        xpLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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

                questViewModel.setLevelRequirements(changed);
                updateXpCard();
            }
        });



        // 계급 바꾸면 해야할 것 표시
        binding.questRankTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull RadioGroup radioGroup, int checkedId) {
                // 계급 바꾸면 뷰모델에 값을 대입해서 바뀐 사실 알리기
                if (checkedId == R.id.quest_rank_tab_private) {
                    questViewModel.setSelectedRank(QuestRank.PRIVATE);
                } else if (checkedId == R.id.quest_rank_tab_first_class) {
                    questViewModel.setSelectedRank(QuestRank.FIRST_CLASS);
                } else if (checkedId == R.id.quest_rank_tab_corporal) {
                    questViewModel.setSelectedRank(QuestRank.CORPORAL);
                } else if (checkedId == R.id.quest_rank_tab_sergeant) {
                    questViewModel.setSelectedRank(QuestRank.SERGEANT);
                }

            }
        });

        // 편집 권한에 따라 보이는 요소들 처리
        if(canEditQuest) {
            // 퀘스트 묶음 추가 버튼 리스너
            binding.questBundleAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bundleCreateLauncher.launch(new Intent(QuestActivity.this, QuestBundleCreateActivity.class)
                            .putExtra("rank", rank.name())
                    );
                }
            });
        }
        else {
            binding.questBundleAdd.setVisibility(View.GONE);
        }

        // Xp 텍스트 리스너 (XP 상세 내용 액티비티 접근)
        binding.questXpSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent xpIntent = new Intent(QuestActivity.this, QuestXpDetailActivity.class)
                        .putExtra("totalXp", questViewModel.getCompletedQuestXp() + 2000L)
                        .putExtra("totalQuestRewardXp", questViewModel.getTotalQuestRewardXp())
                        .putIntegerArrayListExtra("levelRequirements", questViewModel.getLevelRequirements())
                        .putExtra("canEditQuest", canEditQuest);
                xpLauncher.launch(xpIntent);
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

    private void updateQuestCount(ItemQuestBundleBinding card, List<QuestProgress> questProgresses) {
        int totalQuestCount = questProgresses.size();
        int totalQuestCompleted = countCompleted(questProgresses);
        card.questBundleProgress.setText(getString(R.string.quest_bundle_progress_format, totalQuestCompleted, totalQuestCount));
    }

    // 클릭시 할 일, 퀘스트를 받아서 인텐트 객체를 만든다음 extra값에 퀘스트 정보를 넣음
    private void clickQuest(Quest quest) {
        if(this.canEditQuest) {
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

    private void showBundlesForRank(QuestRank rank) {
        if(rank == QuestRank.PRIVATE) {
            binding.questRankHeading.setText(getString(R.string.quest_rank_heading_private));
        } else if(rank == QuestRank.FIRST_CLASS) {
            binding.questRankHeading.setText(getString(R.string.quest_rank_heading_first_class));
        } else if(rank == QuestRank.CORPORAL) {
            binding.questRankHeading.setText(getString(R.string.quest_rank_heading_corporal));
        } else if(rank == QuestRank.SERGEANT) {
            binding.questRankHeading.setText(getString(R.string.quest_rank_heading_sergeant));
        }

        List<QuestProgress> rankProgresses = new ArrayList<>();


        binding.questBundleContainer.removeAllViews();
        for(QuestBundle bundle : questBundles) {
            if(bundle.getRank() != rank)
                continue;

            ItemQuestBundleBinding card = ItemQuestBundleBinding.inflate(getLayoutInflater(), binding.questBundleContainer, false);

            card.questBundleName.setText(bundle.getName());
            if(bundle.getDescription().isEmpty())
                card.questBundleDescription.setVisibility(View.GONE);
            else {
                card.questBundleDescription.setVisibility(View.VISIBLE);
                card.questBundleDescription.setText(bundle.getDescription());
            }

            List<Quest> bundleQuests = new ArrayList<>();
            List<QuestProgress> bundleProgresses = new ArrayList<>();

            for (Quest quest : quests) {
                if (!bundle.getBundleId().equals(quest.getBundleId())) {
                    continue;
                }

                for (QuestProgress progress : questProgresses) {
                    if (quest.getQuestId().equals(progress.getQuestId())) {
                        bundleQuests.add(quest);
                        bundleProgresses.add(progress);
                        break;
                    }
                }
            }

            rankProgresses.addAll(bundleProgresses);

            QuestAdapter adapter = new QuestAdapter(
                    bundleQuests,
                    bundleProgresses,
                    () -> {
                        updateQuestCount(card, bundleProgresses);
                        binding.questRankProgress.setText(getString(R.string.quest_rank_progress_format, countCompleted(rankProgresses),rankProgresses.size()));
                        updateXpCard();
                    },
                    quest -> clickQuest(quest)
            );

            card.questRowRecycler.setLayoutManager(new LinearLayoutManager(this));
            card.questRowRecycler.setAdapter(adapter);
            updateQuestCount(card, bundleProgresses);

            binding.questBundleContainer.addView(card.getRoot());

            if (this.canEditQuest) {
                // 퀘스트 추가 버튼 보이게 설정, 리스너 등록
                card.questAddToBundle.setVisibility(View.VISIBLE);
                card.questAddToBundle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launcher.launch(new Intent(QuestActivity.this, QuestEditorActivity.class)
                                .putExtra("isCreateMode", true)
                                .putExtra("bundleId", bundle.getBundleId())
                                .putExtra("bundleName", bundle.getName())
                        );
                    }
                });

                // 묶음 편집 보이게 설정, 리스너 등록
                card.questBundleEdit.setVisibility(View.VISIBLE);
                card.questBundleEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Bundle> questItems = new ArrayList<>();
                        for (int i = 0; i < bundleQuests.size(); i++) {
                            Quest quest = bundleQuests.get(i);
                            QuestProgress progress = bundleProgresses.get(i);

                            Bundle item = new Bundle();
                            item.putString("questId", quest.getQuestId());
                            item.putString("title", quest.getTitle());
                            item.putBoolean("completed", progress.isCompleted());

                            questItems.add(item);
                        }

                        bundleEditLauncher.launch(new Intent(QuestActivity.this, QuestBundleEditorActivity.class)
                                .putExtra("bundleId", bundle.getBundleId())
                                .putExtra("bundleName", bundle.getName())
                                .putExtra("bundleDescription", bundle.getDescription())
                                .putExtra("bundleRank", bundle.getRank().name())
                                .putParcelableArrayListExtra("questItems", questItems)
                        );
                    }
                });
            } else {
                card.questAddToBundle.setVisibility(View.GONE);
                card.questBundleEdit.setVisibility(View.GONE);
            }

        }

        updateXpCard();
        binding.questRankProgress.setText(getString(R.string.quest_rank_progress_format, countCompleted(rankProgresses),rankProgresses.size()));
    }

    // 퀘스트 진행 리스트에서 완료한 개수 새기
    private int countCompleted(List<QuestProgress> progresses) {
        int count = 0;
        for(QuestProgress progress : progresses)
            if (progress.isCompleted())
                count++;
        return count;
    }

    // 아이디에 해당하는 퀘스트 지우기
    private void removeQuestsFromBundle(String bundleId, List<String> deletedQuestIds) {
        // 왜 뒤에서 부터 도냐? 앞에서 부터 돌면 리스트 내에서 하나 지우면 순서가 앞으로 당겨져서 혼동
        for (int i = quests.size() - 1; i >= 0; i--) {
            Quest quest = quests.get(i);
            String questId = quest.getQuestId();

            // 아이디 같은 퀘스트 발견시.(묶음 아이디가 같아야하고, 지우는 퀘스트 아이디 목록에 퀘스트 아이디가 있을경우)
            if (bundleId.equals(quest.getBundleId()) && deletedQuestIds.contains(questId)) {
                // 퀘스트 지우고, 퀘스트 진행사항(퀘스트 아이디와 같은 진행사항) 지우기
                quests.remove(i);
                questProgresses.removeIf(progress -> questId.equals(progress.getQuestId()));
            }
        }
    }

    // 퀘스트 순서 변경 반영 메서드
    private void applyQuestOrder(String bundleId, List<String> orderedQuestIds) {
        List<Integer> positions = new ArrayList<>();
        Map<String, Quest> questsById = new HashMap<>();

        // 이 묶음이 차지한 자리와 기존 Quest 객체를 찾음
        for (int i = 0; i < quests.size(); i++) {
            Quest quest = quests.get(i);

            if (bundleId.equals(quest.getBundleId())) {
                positions.add(i);
                questsById.put(quest.getQuestId(), quest);
            }
        }

        // 저장된 ID가 현재 묶음의 남은 퀘스트와 정확히 일치하는지 확인
        if (orderedQuestIds.size() != positions.size() || !questsById.keySet().equals(new HashSet<>(orderedQuestIds))) {
            return;
        }


        // 이 묶음의 자리만 새 순서대로 채움
        for (int i = 0; i < positions.size(); i++) {
            Quest quest = questsById.get(orderedQuestIds.get(i));
            quests.set(positions.get(i), quest);
        }
    }

    // 묶음 내 퀘스트 전체 및 묶음 삭제 메서드
    private void deleteQuestBundle(QuestBundle bundle) {
        String bundleId = bundle.getBundleId();
        List<String> questIds = new ArrayList<>();

        for (Quest quest : quests) {
            if (bundleId.equals(quest.getBundleId())) {
                questIds.add(quest.getQuestId());
            }
        }

        removeQuestsFromBundle(bundleId, questIds);
        questBundles.remove(bundle);
    }

    // 상단 xp 및 레벨 카드 업데이트 showBundlesForRank 맨 마지막에, checkedClickListener 마지막부분에 쓰임
    private void updateXpCard() {
        long totalXp = questViewModel.getCompletedQuestXp() + 2000L;
        ArrayList<Integer> levelRequirements = questViewModel.getLevelRequirements();

        long currentXp = totalXp;
        int level = 1;
        int nextLevelXp = 0;
        boolean isMaxLevel = true;
        for(int levelRequirement : levelRequirements) {
            // 현재 레벨 도달시에 (누적xp에서 필요xp를 계속 빼는데, 빼는 값이 필요 xp보다 작아질 경우)
            if(levelRequirement > currentXp) {
                nextLevelXp = levelRequirement;
                isMaxLevel = false;
                break;
            }
            currentXp -= levelRequirement;
            level++;
        }

        if(isMaxLevel && !levelRequirements.isEmpty()) {
            nextLevelXp = levelRequirements.get(levelRequirements.size() - 1);
            currentXp += nextLevelXp;
            binding.questLevel.setText(R.string.quest_xp_detail_max_level);
        } else {
            binding.questLevel.setText(getString(R.string.quest_xp_current_level_format, level));
        }

        binding.questXpSettings.setText(getString(R.string.quest_xp_current_xp_format, currentXp, nextLevelXp));

        binding.questXpProgress.setMax(nextLevelXp);
        binding.questXpProgress.setProgressCompat((int)currentXp, true);
    }
}
