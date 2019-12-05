package com.mayabo.finalandroidproject.chargestationfinder;

public class Record {
    private String title;
    private String longitude;
    private String latitude;
    private String contact;
    private String address;
    private Double distance;
    private boolean isFavorite;

    /**
     * Creates an new instant of Record with specified values.
     * @param title title of this Record
     * @param contact contact of this Record
     * @param address address of this Record
     * @param latitude latitude of this Record
     * @param longitude longitude of this Record
     * @param isFavorite if this Record is favorite
     */
    public Record(String title, String contact, String address, String latitude, String longitude, boolean isFavorite) {
        setTitle(title);
        setContact(contact);
        setAddress(address);
        setLatitude(latitude);
        setLongitude(longitude);
        setIsFavorite(isFavorite);
        setDistance(null);
    }

    /**
     *
     * @return title of this Record
     */
    public String getTitle() {
        if (title == null) {
            return null;
        }
        return title.equals("null") ? "unavailable" : title;
    }

    /**
     *
     * @param title title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return longitude of this Record
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude longitude to set
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return latitude of this Record
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude latitude to set
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return contact of this Record
     */
    public String getContact() {
        if (contact == null) {
            return null;
        }
        return contact.equals("null") ? null : contact;
    }

    /**
     *
     * @param contact contact to set
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     *
     * @return if this Record is favorite
     */
    public boolean isFavorite() {
        return isFavorite;
    }

    /**
     *
     * @param favorite attribute to set
     */
    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    /**
     *
     * @return distance of this Record
     */
    public Double getDistance() {
        return distance;
    }

    /**
     *
     * @param distance distance to set
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    /**
     *
     * @return address of this Record
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @param other Record to compare with
     * @return true if two Records are equal
     */
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
