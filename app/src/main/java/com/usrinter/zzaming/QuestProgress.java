package com.usrinter.zzaming;

public class QuestProgress {
    private String questId;
    private boolean completed;

    public QuestProgress(String questId) {
        this.questId = questId;
        this.completed = false;
    }
    public QuestProgress(String questId, boolean completed) {
        this.questId = questId;
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getQuestId() {
        return questId;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }
}
