package com.mayabo.finalandroidproject.currency;

/**
 * This class is to define favourite pair of currency that user wants to save in database
 *
 */
public class FavoriteItem {
    private String to;
    private String from;
    private long id;


    /**
     * FavouriteItem constructor using 3 arguments from, to, id
     * @param from: String
     * @param to: String
     * @param id: long
     *
     */
    public FavoriteItem(String from, String to, long id){
        this.id= id;
        this.from=from;
        this.to=to;
    }

    /**
     * FavouriteItem constructor using 2 arguments from, to
     * @param from: String.
     * @param to: String.
     *
     */
    public FavoriteItem(String from, String to) {
        this(from, to, 0);
    }


    /**
     * Set the currency code the user want to convert.
     * @param from: String.
     */
    public void setFrom(String from) {this.from= from;}

    /**
     * Get the currency code the user want to convert.
     * @return from: String.
     */
    public String getFrom () {return this.from;}

    /**
     * Set the currency code the user want to convert to.
     * @param to: String.
     */
    public void setTo (String to) {this.to = to;}

    /**
     * Get the currency code the user want to convert to.
     * @return to: String.
     */
    public String getTo () {return this.to;}

    /**
     * Set the id of the favourite conversion.
     * @param id: long.
     */
    public void setId (long id) {this.id = id;}

    /**
     * Get the id of the favourite conversion.
     * @return id: long.
     */
    public long getId () {return id;}

    /**
     * Compare the two pairs of String from, to.
     * @param other: Object.
     * @return boolean:
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FavoriteItem)) {
            return false;
        }
        FavoriteItem item = (FavoriteItem) other;
        return this.getTo().equals(item.to) && this.getFrom().equals(((FavoriteItem) other).from);
    }
}
