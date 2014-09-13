package net.bibim.database1;

public class Info {
    private int id;
    private String meeting;
    private String time;

    public Info(int id, String meeting, String time) {
        this.id = id;
        this.meeting = meeting;
        this.time = time;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getMeeting() {
        return meeting;
    }
    public void setMeeting(String meeting) {
        this.meeting = meeting;
    }
    public String getTime() {
        return time;
    }
    public void setAge(String time) {
        this.time = time;
    }
    public String toString() {
        return String.format("[%s] %s", meeting, time);
    }
}
