package it.polimi.tiw.TIW_OnlineAuctions_RIA.beans;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Bid {
    private int auction_id;

    private int user_id;
    private String user_first_name;
    private String user_last_name;

    private Instant bid_time;
    private double price;

    public int getAuction_id() {
        return auction_id;
    }

    public void setAuction_id(int auction_id) {
        this.auction_id = auction_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_first_name() {
        return user_first_name;
    }

    public void setUser_first_name(String user_first_name) {
        this.user_first_name = user_first_name;
    }

    public String getUser_last_name() {
        return user_last_name;
    }

    public void setUser_last_name(String user_last_name) {
        this.user_last_name = user_last_name;
    }

    public Instant getBid_time() {
        return bid_time;
    }

    public void setBid_time(Instant bid_time) {
        this.bid_time = bid_time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static String getFormattedPrice(double price) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(price);
    }

    public static String getFormattedDateTime(Instant datetime) {
        DateTimeFormatter formatter = DateTimeFormatter .ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.ITALY)
                .withZone(ZoneId.systemDefault());

        return formatter.format(datetime);
    }
}
