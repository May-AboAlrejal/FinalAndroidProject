package com.mayabo.finalandroidproject.chargestationfinder;

public class Record {
    private String title;
    private String longitude;
    private String latitude;
    private String contact;
    private boolean isFavorite;

    public Record(String title, String contact, String latitude, String longitude, boolean isFavorite) {
        setTitle(title);
        setContact(contact);
        setLatitude(latitude);
        setLongitude(longitude);
        setIsFavorite(isFavorite);
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
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
