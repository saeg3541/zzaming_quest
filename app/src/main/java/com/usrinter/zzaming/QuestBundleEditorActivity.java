package com.usrinter.zzaming;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.usrinter.zzaming.databinding.ActivityQuestBundleEditorBinding;
import com.usrinter.zzaming.databinding.DialogQuestBundleDeleteBinding;

import java.util.ArrayList;

public class QuestBundleEditorActivity extends AppCompatActivity {
    private ActivityQuestBundleEditorBinding binding;
    private final ArrayList<String> deletedQuestIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        //setContentView(R.layout.activity_quest_bundle_editor);

        binding = ActivityQuestBundleEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String bundleId = getIntent().getStringExtra("bundleId");
        String bundleName = getIntent().getStringExtra("bundleName");
        String bundleDescription = getIntent().getStringExtra("bundleDescription");
        String bundleRankText = getIntent().getStringExtra("bundleRank");
        QuestRank bundleRank = (bundleRankText != null) ? QuestRank.valueOf(bundleRankText) : QuestRank.PRIVATE;
        ArrayList<Bundle> questItems = IntentCompat.getParcelableArrayListExtra(getIntent(), "questItems", Bundle.class);
        if (questItems == null) {
            questItems = new ArrayList<>();
        }

        TextView bundleEditorRank = binding.bundleEditorRank;
        if(bundleRank == QuestRank.PRIVATE) {
            bundleEditorRank.setText(R.string.quest_rank_private);
        }
        else if(bundleRank == QuestRank.FIRST_CLASS) {
            bundleEditorRank.setText(R.string.quest_rank_first_class);
        }
        else if(bundleRank == QuestRank.CORPORAL) {
            bundleEditorRank.setText(R.string.quest_rank_corporal);
        }
        else if(bundleRank == QuestRank.SERGEANT) {
            bundleEditorRank.setText(R.string.quest_rank_sergeant);
        }

        binding.bundleEditorNameInput.setText(bundleName);
        binding.bundleEditorDescriptionInput.setText(bundleDescription);

        updateQuestListState(questItems.size());


        // 수정 퀘스트 아이템들 리사이클러
        BundleEditorQuestAdapter adapter = new BundleEditorQuestAdapter(questItems, new BundleEditorQuestAdapter.OnQuestRemovedListener() {
            // 퀘스트 지웠을 때 리스너 구현.
            @Override
            public void onQuestRemoved(String questId, int remainingCount) {
                deletedQuestIds.add(questId);
                updateQuestListState(remainingCount);
            }
        });
        binding.bundleEditorQuestList.setAdapter(adapter);

        // 드래그 콜백 메서드 구현
        ItemTouchHelper.SimpleCallback dragCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
                int from = source.getBindingAdapterPosition();
                int to = target.getBindingAdapterPosition();

                return adapter.moveItem(from, to);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 이 화면에서는 스와이프 동작을 사용하지 않음
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        };
        ItemTouchHelper dragHelper = new ItemTouchHelper(dragCallback);
        dragHelper.attachToRecyclerView(binding.bundleEditorQuestList);
        adapter.setDragHelper(dragHelper);



        // 뒤로가기 버튼 눌렀을 떄 클릭 리스너
        binding.bundleEditorBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // 변경사항 저장 버튼 눌렀을 때 클릭 리스너
        binding.bundleEditorSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bundleName = String.valueOf(binding.bundleEditorNameInput.getText()).trim();
                if (bundleName.isEmpty()) {
                    binding.bundleEditorNameInput.setError("묶음 이름을 입력해 주세요.");
                    binding.bundleEditorNameInput.requestFocus();
                    return;
                }
                String bundleDescription = String.valueOf(binding.bundleEditorDescriptionInput.getText());
                setResult(RESULT_OK, new Intent()
                        .putExtra("bundleId", bundleId)
                        .putExtra("bundleName", bundleName)
                        .putExtra("bundleDescription", bundleDescription.trim())
                        .putStringArrayListExtra("deletedQuestIds", deletedQuestIds)
                        .putStringArrayListExtra("orderedQuestIds", adapter.getOrderedQuestIds())
                );
                finish();
            }
        });

        // 퀘스트 묶음 삭제 버튼 리스너
        binding.bundleEditorDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBundleDeleteDialog(bundleId, bundleName);
            }
        });



    }

    // 퀘스트 리스트 상태 업데이트, 다 지워서 없을 떄 표시할 수 있음
    private void updateQuestListState(int remainingCount) {
        binding.bundleEditorItemCount.setText(getString(R.string.quest_bundle_item_count, remainingCount));

        if (remainingCount == 0) {
            binding.bundleEditorEmpty.setVisibility(View.VISIBLE);
            binding.bundleEditorQuestList.setVisibility(View.GONE);
        } else {
            binding.bundleEditorEmpty.setVisibility(View.GONE);
            binding.bundleEditorQuestList.setVisibility(View.VISIBLE);
        }
    }

    // 묶음 삭제 다이얼로그 띄우기
    private void showBundleDeleteDialog(String bundleId, String bundleName) {
        DialogQuestBundleDeleteBinding dialogBinding = DialogQuestBundleDeleteBinding.inflate(getLayoutInflater());

        if (bundleName != null && !bundleName.trim().isEmpty()) {
            String message = getString(R.string.quest_bundle_delete_named_message, bundleName);
            dialogBinding.bundleDeleteMessage.setText(message);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.bundleDeleteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogBinding.bundleDeleteConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                result.putExtra("bundleId", bundleId);
                result.putExtra("deleteBundle", true);

                setResult(RESULT_OK, result);
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}