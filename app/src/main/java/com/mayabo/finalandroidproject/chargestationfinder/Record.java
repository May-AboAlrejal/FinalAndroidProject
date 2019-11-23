package com.mayabo.finalandroidproject.chargestationfinder;

public class Record {
    private String title;
    private String longitude;
    private String latitude;
    private String contact;

    public Record(String title, String contact, String latitude, String longitude) {
        setTitle(title);
        setContact(contact);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public String getTitle() {
        return title.equals("null") ? "unavailable" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getContact() {
        return contact.equals("null") ? "unavailable" : contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
