package it.polimi.tiw.TIW_OnlineAuctions_RIA.beans;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Auction {
    private int auction_id;
    private int user_id;
    private String name;
    private String description;
    private String image;
    private double starting_price;
    private double min_price_gap;
    private Instant end_date;
    private boolean closed;
    private boolean expired;

    private double winning_price;
    private int winner_id;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getStarting_price() {
        return starting_price;
    }

    public void setStarting_price(double starting_price) {
        this.starting_price = starting_price;
    }

    public double getMin_price_gap() {
        return min_price_gap;
    }

    public void setMin_price_gap(double min_price_gap) {
        this.min_price_gap = min_price_gap;
    }

    public Instant getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Instant end_date) {
        this.end_date = end_date;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public double getWinning_price() {
        return winning_price;
    }

    public void setWinning_price(double winning_price) {
        this.winning_price = winning_price;
    }

    public int getWinner_id() {
        return winner_id;
    }

    public void setWinner_id(int winner_id) {
        this.winner_id = winner_id;
    }

    public String getTimeLeft(Instant lastLogin) {
        Duration duration = Duration.between(lastLogin, getEnd_date());

        if(duration.getSeconds() < 0)
            return "Expired";

        return duration.toDaysPart() + " days " + duration.toHoursPart() + " hours";
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


