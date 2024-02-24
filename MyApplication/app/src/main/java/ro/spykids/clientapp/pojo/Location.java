package ro.spykids.clientapp.pojo;

import java.time.LocalDateTime;

public class Location {

    private String email;

    private Double latitude;

    private Double longitude;

    private LocalDateTime arrivalTime;

    private LocalDateTime departureTime;

    private String message;

    public Location() {
    }

    public Location(String email, Double latitude, Double longitude, LocalDateTime arrivalTime, LocalDateTime departureTime, String message) {
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

