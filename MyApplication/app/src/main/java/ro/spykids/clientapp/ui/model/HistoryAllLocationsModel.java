package ro.spykids.clientapp.ui.model;

public class HistoryAllLocationsModel {
    private String address;
    private String arrival_time;
    private String departure_time;
    private String email;
    private Double longitude;
    private Double latitude;


    public HistoryAllLocationsModel(){}

    public HistoryAllLocationsModel(String address, String arrival_time, String departure_time, String email, Double longitude, Double latitude) {
        this.address = address;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.email = email;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }


    public String getEmail() {
        return email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
