package com.safjnest.Utilities.Guild;

import java.time.LocalDateTime;

import com.safjnest.Utilities.SQL.DatabaseHandler;

public class UserData {
    private int ID;
    private final long USER_ID;
    private int experience;
    private int level;
    private int messages;
    private int updateTime;
    private LocalDateTime lastMessageTime;
    private GuildData guildData;

    public UserData(int ID, long USER_ID, int experience, int level, int messages, int updateTime, GuildData guildData) {
        this.ID = ID;
        this.USER_ID = USER_ID;
        this.experience = experience;
        this.level = level;
        this.messages = messages;
        this.updateTime = updateTime;
        this.lastMessageTime = LocalDateTime.now().minusSeconds(updateTime);
        this.guildData = guildData;
    }

    public UserData(long USER_ID, GuildData guildData) {
        this.ID = 0;
        this.USER_ID = USER_ID;
        this.experience = 0;
        this.level = 1;
        this.messages = 0;
        this.updateTime = 60;
        this.lastMessageTime = LocalDateTime.now().minusSeconds(updateTime);
        this.guildData = guildData;
    }

    private void handleEmptyID() {
        if (this.ID == 0) {
            System.out.println("[CACHE] Pushing local UserData into Database=> " + this.USER_ID);
            this.ID = DatabaseHandler.insertUserData(guildData.getId(), this.USER_ID);
        }
    }

    public int getID() {
        return ID;
    }

    public long getUserId() {
        return USER_ID;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public int getMessages() {
        return messages;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public boolean canReceiveExperience() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(lastMessageTime.plusSeconds(updateTime))) {
            lastMessageTime = now;
            return true;
        }
        return false;
    }

    public boolean setExpData(int experience, int level) {
        handleEmptyID();
        this.messages++;
        boolean result = DatabaseHandler.updateUserDataExperience(this.ID, experience, level, this.messages);

        if (result) {
            this.experience = experience;
            this.level = level;
        }
        else {
            this.messages--;
        }
        return result;

    }

    public boolean setUpdateTime(int updateTime) {
        handleEmptyID();
        boolean result = DatabaseHandler.updateUserDataUpdateTime(this.ID, updateTime);
        if (result) {
            this.updateTime = updateTime;
        }
        return result;
    }



    @Override
    public String toString() {
        return "UserData{" +
                "ID=" + ID +
                ", USER_ID=" + USER_ID +
                ", experience=" + experience +
                ", level=" + level +
                ", messages=" + messages +
                ", updateTime=" + updateTime +
                ", lastMessageTime=" + lastMessageTime +
                '}';
    }

}
