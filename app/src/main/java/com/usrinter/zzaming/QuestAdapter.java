package com.usrinter.zzaming;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usrinter.zzaming.databinding.ItemQuestBinding;

import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    public interface OnQuestCheckListener {
        // 익명함수 받도록 구현했는데 이게 되네?????? 개신기하다
        void onQuestCheckedChanged();
    }
    public interface OnQuestClickListener {
        void onQuestClick(Quest quest);
    }

    private final List<Quest> quests;
    private final List<QuestProgress> questProgresses;
    //리스너 객체(익명함수 형태로 전해져옴) 받아옴
    private final OnQuestCheckListener checkListener;
    private final OnQuestClickListener clickListener;

    public QuestAdapter(List<Quest> quests, List<QuestProgress> questProgresses, OnQuestCheckListener checkListener, OnQuestClickListener clickListener) {
        this.quests = quests;
        this.questProgresses = questProgresses;
        this.checkListener = checkListener;
        this.clickListener = clickListener;
    }

    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        private final ItemQuestBinding binding;
        public QuestViewHolder(@NonNull ItemQuestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindQuestTitle(String questTitle) {
            binding.questItemTitle.setText(questTitle);
        }
        private void bindQuestDescription(String questDescription) {
            binding.questItemDetails.setText(questDescription);
        }
        private void bindQuestCheckBox(QuestProgress progress, OnQuestCheckListener listener) {
            // 리사이클 할떄 이전 없어진 리스너 재사용을 방지, 체크 형태도 현재 형태로
            binding.questItemCheck.setOnCheckedChangeListener(null);
            binding.questItemCheck.setChecked(progress.isCompleted());
            binding.questItemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean isChecked) {
                    progress.setCompleted(isChecked);
                    if (listener != null) {
                        listener.onQuestCheckedChanged();
                    }
                }
            });
        }
        // 퀘스트 제목이나 > 문자 클릭했을 떄 넘어가는 이벤트 할당 함수.
        // 퀘스트 객체와 퀘스트 클릭 리스너를 받음
        private void clickQuestEvent(Quest quest, OnQuestClickListener listener) {
            View.OnClickListener clickEvent = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 받은 퀘스트로 클릭시 처리 이벤트 실행
                    listener.onQuestClick(quest);
                }
            };

            binding.questItemTitle.setOnClickListener(clickEvent);
            binding.questRightIcon.setOnClickListener(clickEvent);
        }

    }
    // 뷰 inflate해서 뷰 홀더 객체에 담고 리턴
    // 뷰홀더가 약간 상자? 같은거라고 하셨던거 같은데
    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuestBinding binding = ItemQuestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new QuestViewHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        // position의 퀘스트 및 퀘스트 진행상황 객체 불러오기
        Quest quest = quests.get(position);
        QuestProgress progress = questProgresses.get(position);

        // item_quest 레이아웃에 제목, 설명
        holder.bindQuestTitle(quest.getTitle());
        holder.bindQuestDescription(holder.itemView.getContext().getString(R.string.quest_item_reward_format, quest.getCategory().getName(), quest.getRewardXp()));
        // 체크박스 눌렀을시 할 기능
        holder.bindQuestCheckBox(progress, checkListener);
        // 퀘스트 >기호나 제목누르면 할 기능, 퀘스트를 넣었으니 해당 퀘스트가 가겠지?
        holder.clickQuestEvent(quest, clickListener);


    }

    @Override
    public int getItemCount() {
        return quests.size();
    }
}
