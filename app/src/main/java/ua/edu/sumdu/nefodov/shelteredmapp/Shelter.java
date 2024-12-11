package ua.edu.sumdu.nefodov.shelteredmapp;

import java.util.List;

public class Shelter {

    public int id;

    private double latitude;
    private double longitude;
    private ShelterStatus status;
    private List<ShelterCondition> conditions;
    private int capacity;
    private double area;
    private String additional;

    public Shelter() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public ShelterStatus getStatus() {
        return status;
    }

    public void setStatus(ShelterStatus status) {
        this.status = status;
    }

    public List<ShelterCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<ShelterCondition> conditions) {
        this.conditions = conditions;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status=" + status +
                ", conditions=" + conditions +
                ", capacity=" + capacity +
                ", area=" + area +
                ", additional='" + additional + '\'' +
                '}';
    }
}
