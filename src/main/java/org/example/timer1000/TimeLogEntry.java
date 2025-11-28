package org.example.timer1000;

public class TimeLogEntry {
    private int memberId;
    private long durationSeconds;

    public TimeLogEntry() {}

    public TimeLogEntry(int memberId, long durationSeconds) {
        this.memberId = memberId;
        this.durationSeconds = durationSeconds;
    }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }
}
