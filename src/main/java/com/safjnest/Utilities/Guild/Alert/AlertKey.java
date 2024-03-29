package com.safjnest.Utilities.Guild.Alert;

import java.util.Objects;

public class AlertKey {
    private AlertType type;
    private int level;

    public AlertKey(AlertType type, int level) {
        this.type = type;
        this.level = level;
    }

    public AlertKey(AlertType type) {
        this.type = type;
        this.level = 0;
    }

    public AlertType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "AlertKey [" +
                "type=" + type +
                ", level=" + level +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertKey alertKey = (AlertKey) o;
        return level == alertKey.level && type == alertKey.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, level);
    }


}
