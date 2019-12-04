package com.mayabo.finalandroidproject.currency;

/**
 * This class is used in the Dialog showing the information of the conversion which user has just
 * converted
 */
public class Rate {
    private String currencyCode;
    private String currencyBase;
    private double currencyRate;
    private String dateOfConversion;


    /**
     * The constructor using 4 parameters
     * @param currencyCode: String
     * @param currencyBase: String
     * @param currencyRate: double
     * @param dateOfConversion: String
     */
    public Rate(String currencyCode, String currencyBase, double currencyRate, String dateOfConversion) {
        this.currencyCode = currencyCode;
        this.currencyBase = currencyBase;
        this.currencyRate = currencyRate;
        this.dateOfConversion = dateOfConversion;
    }

    /**
     * This method get the currency that user want to convert to
     * @return currencyCode: String
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * This method set the currency that user want to convert to
     * @param currencyCode: String
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * This method get the currency that user want to convert
     * @return currencyBase: String
     */
    public String getCurrencyBase() {
        return currencyBase;
    }

    /**
     * This method set the currency that user want to convert
     * @param currencyBase: String
     */
    public void setCurrencyBase(String currencyBase) {
        this.currencyBase = currencyBase;
    }

    /**
     * This method get the currency rate
     * @return currencyRate: double
     */
    public double getCurrencyRate() {
        return currencyRate;
    }

    /**
     * This method set the currency rate. It's unused in this case
     * @param currencyRate: double
     */
    public void setCurrencyRate(double currencyRate) {
        this.currencyRate = currencyRate;
    }

    /**
     * This method get the date of conversion
     * @return dateOfConversion: String
     */
    public String getDateOfConversion() {
        return dateOfConversion;
    }

    /**
     * This method set the date of conversion.It's unused in this case
     * @param dateOfConversion
     */
    public void setDateOfConversion(String dateOfConversion) {
        this.dateOfConversion = dateOfConversion;
    }

    /**
     * This method is to convert object from Rate class to FavoriteItem class
     * @return FavoriteItem
     */
    public FavoriteItem toFavoriteItem() {
        return new FavoriteItem(this.getCurrencyBase(), this.getCurrencyCode());
    }
}
