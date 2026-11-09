package com.usrinter.zzaming;

public class Quest {
    private String questId;
    private String bundleId;
    private String title;
    private String description;
    private int rewardXp;

    public Quest(String questId, String bundleId, String title, String description, int rewardXp) {
        this.questId = questId;
        this.bundleId = bundleId;
        this.title = title;
        this.description = description;
        this.rewardXp = rewardXp;
    }

    public String getQuestId() {
        return questId;
    }

    public String getBundleId() {
        return bundleId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getRewardXp() {
        return rewardXp;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRewardXp(int rewardXp) {
        this.rewardXp = rewardXp;
    }
}
