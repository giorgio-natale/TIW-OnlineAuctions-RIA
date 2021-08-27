package it.polimi.tiw.TIW_OnlineAuctions_RIA.beans;

import java.time.Instant;
import java.time.ZoneId;

public class User {
    private int user_id;
    //password is omitted on purpose
    private String email;
    private String first_name;
    private String last_name;
    private String street;
    private String city;
    private String province;
    private String zip_code;
    private String other_address_infos;
    private Instant last_login;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getOther_address_infos() {
        return other_address_infos;
    }

    public void setOther_address_infos(String other_address_infos) {
        this.other_address_infos = other_address_infos;
    }

    public Instant getLast_login() {
        return last_login;
    }

    public void setLast_login(Instant last_login) {
        this.last_login = last_login;
    }

    public static int getTimeOfDay(Instant instant) {
        int hour = instant.atZone(ZoneId.of("Europe/Rome")).getHour();

        if(hour >= 6 && hour <= 12)
            return 0;
        else if(hour > 12 && hour <= 18)
            return 1;
        else if(hour > 18 && hour <= 21)
            return 2;
        else
            return 3;
    }
}
