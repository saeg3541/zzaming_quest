package com.usrinter.zzaming;

public enum QuestCategory {
    LIFE("생활"),
    ADMIN("행정"),
    DUTY("직무"),
    OTHER("기타");

    final private String name;
    private QuestCategory(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
