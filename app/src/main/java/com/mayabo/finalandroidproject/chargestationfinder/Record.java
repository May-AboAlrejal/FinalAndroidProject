package com.mayabo.finalandroidproject.chargestationfinder;

public class Record {
    private String title;
    private String longitude;
    private String latitude;
    private String contact;
    private String address;
    private Double distance;
    private boolean isFavorite;

    public Record(String title, String contact, String address, String latitude, String longitude, boolean isFavorite) {
        setTitle(title);
        setContact(contact);
        setAddress(address);
        setLatitude(latitude);
        setLongitude(longitude);
        setIsFavorite(isFavorite);
        setDistance(null);
    }

    public String getTitle() {
        if (title == null) {
            return null;
        }
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
        if (contact == null) {
            return null;
        }
        return contact.equals("null") ? null : contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Record)) {
            return false;
        }
        Record record = (Record) other;
        return this.title.equals(record.title) &&
            this.latitude.equals(record.latitude) &&
            this.longitude.equals(record.longitude);
    }
}
