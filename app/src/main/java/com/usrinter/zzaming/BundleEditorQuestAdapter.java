package com.usrinter.zzaming;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.usrinter.zzaming.databinding.ItemBundleEditorQuestBinding;

import java.util.ArrayList;
import java.util.List;

public class BundleEditorQuestAdapter extends RecyclerView.Adapter<BundleEditorQuestAdapter.QuestViewHolder> {

    // 퀘스트 지우면 할 리스너, questId와 남은 퀘스트 개수 알려줌 리스너
    public interface OnQuestRemovedListener {
        void onQuestRemoved(String questId, int remainingCount);
    }

    // 필드로 퀘스트 아이템 번들 리스트 레퍼런스 받을 변수
    private final List<Bundle> questItems;
    private final OnQuestRemovedListener removedListener;
    private ItemTouchHelper dragHelper;

    public void setDragHelper(ItemTouchHelper dragHelper) {
        this.dragHelper = dragHelper;
    }

    // 어뎁터 생성자
    public BundleEditorQuestAdapter(List<Bundle> questItems, OnQuestRemovedListener removedListener) {
        this.questItems = questItems;
        this.removedListener = removedListener;
    }

    // 뷰 홀더 클래스 구현 영역 시작
    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        // 바인딩 저장 변수
        private final ItemBundleEditorQuestBinding binding;

        // 뷰홀더 생성자
        public QuestViewHolder(ItemBundleEditorQuestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // 완료 텍스트 설정 메서드
        public void bind(Bundle item) {
            binding.bundleQuestTitle.setText(item.getString("title"));

            if (item.getBoolean("completed")) {
                binding.bundleQuestStatus.setText(R.string.quest_status_done);
            } else {
                binding.bundleQuestStatus.setText(R.string.quest_bundle_item_pending);
            }
        }
    }
    // 뷰 홀더 구현 영역 끝


    // 뷰 홀더 객체 새로 생성 (퀘스트 수정 아이템 하나 인플레이션 해서 객체값 담음)
    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBundleEditorQuestBinding binding = ItemBundleEditorQuestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new QuestViewHolder(binding);
    }

    // 뷰 홀더 객체내의 아이템 할 일
    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Bundle item = questItems.get(position);
        holder.bind(item);


        holder.binding.bundleQuestDragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN && dragHelper != null) {
                    dragHelper.startDrag(holder);
                }
                return false;
            }
        });


        holder.binding.bundleQuestDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getBindingAdapterPosition();

                if (currentPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                Bundle removedItem = questItems.remove(currentPosition);
                notifyItemRemoved(currentPosition);

                removedListener.onQuestRemoved(removedItem.getString("questId"), questItems.size());
            }
        });
    }

    // 아이템 개수
    @Override
    public int getItemCount() {
        return questItems.size();
    }

    public boolean moveItem(int from, int to) {
        int count = questItems.size();

        if (from < 0 || from >= count || to < 0 || to >= count || from == to) {
            return false;
        }

        Bundle movedItem = questItems.remove(from);
        questItems.add(to, movedItem);
        notifyItemMoved(from, to);

        return true;
    }

    public ArrayList<String> getOrderedQuestIds() {
        ArrayList<String> questIds = new ArrayList<>();

        for (Bundle item : questItems) {
            questIds.add(item.getString("questId"));
        }

        return questIds;
    }
}