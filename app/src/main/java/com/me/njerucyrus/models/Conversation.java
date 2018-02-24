package com.me.njerucyrus.models;

/**
 * Created by njerucyrus on 2/23/18.
 */

public class Conversation {
    private boolean seen;
    private long timestamp;

    public Conversation() {
    }

    public Conversation(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
