package ro.spykids.clientapp.pojo;

import java.time.LocalDateTime;

public class Battery {
    private Integer percent;
    private LocalDateTime time;
    private String childEmail;
    private String message;

    public Battery(){}

    public Battery(Integer percent, LocalDateTime time, String childEmail, String message) {
        this.percent = percent;
        this.time = time;
        this.childEmail = childEmail;
        this.message = message;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getChildEmail() {
        return childEmail;
    }

    public void setChildEmail(String childEmail) {
        this.childEmail = childEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
