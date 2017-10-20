package com.example.calendarquickstart;

import com.google.api.services.calendar.model.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jerry on 19/10/17.
 */

public class Reservation {

    private static final SimpleDateFormat ISO8601 = new SimpleDateFormat("HH:mma", Locale.US);

    private boolean booked;
    private Date currentTime;
    private String reservationRoom;
    private Date reservationStart;
    private Date reservationEnd;
    private Event.Organizer reservationOwner;
    private String reservationTitle;

    public Reservation(Date currentTime) {
        booked = false;
        this.currentTime = currentTime;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public String getReservationRoom() {
        // TODO: Move the room name to the Settings model
        if (reservationRoom == null) {
            return "Bashir";
        }

        return reservationRoom.replace("MR_", "");
    }

    public void setReservationRoom(String reservationRoom) {
        this.reservationRoom = reservationRoom;
    }

    public String getReservationOwner() {
        return reservationOwner.getDisplayName() != null ?
                reservationOwner.getDisplayName() :
                getWordsCapitalised(reservationOwner.getEmail()
                        .replace("@fluxfederation.com", "")
                        .replace(".", " "));
    }

    public void setReservationOwner(Event.Organizer reservationOwner) {
        this.reservationOwner = reservationOwner;
    }

    public String getReservationTitle() {
        return getWordsCapitalised(reservationTitle);
    }

    public void setReservationTitle(String reservationTitle) {
        this.reservationTitle = reservationTitle;
    }

    public String getCurrentTime() {
        return getDateString(currentTime);
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public void setReservationStart(Date reservationStart) {
        this.reservationStart = reservationStart;
    }

    public void setReservationEnd(Date reservationEnd) {
        this.reservationEnd = reservationEnd;
    }

    public String getReservationTime() {
        return String.format("%s - %s", getDateString(reservationStart), getDateString(reservationEnd));
    }

    public String getDateString(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        return ISO8601.format(dateTime).toLowerCase();
    }

    public String getWordsCapitalised(String stringToCapitalise) {
        String[] strArray = stringToCapitalise.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }
}
