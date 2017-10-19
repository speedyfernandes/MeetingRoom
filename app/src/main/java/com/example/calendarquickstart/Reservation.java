package com.example.calendarquickstart;

import com.google.api.services.calendar.model.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jerry on 19/10/17.
 */

public class Reservation {

    private static final SimpleDateFormat ISO8601 = new SimpleDateFormat("HH:mm a", Locale.US);

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
        if(reservationRoom == null) {
            return "Bashir";
        }

        return reservationRoom.replace("MR_", "");
    }

    public void setReservationRoom(String reservationRoom) {
        this.reservationRoom = reservationRoom;
    }

    public String getReservationOwner() {
        // TODO: Move this to the view layer
        if(reservationOwner == null) {
            return "Google Calendars";
        }

        return reservationOwner.getDisplayName() != null ?
                reservationOwner.getDisplayName() :
                reservationOwner.getEmail().replace("@fluxfederation.com", "").replace(".", " ");
    }

    public void setReservationOwner(Event.Organizer reservationOwner) {
        this.reservationOwner = reservationOwner;
    }

    public String getReservationTitle() {
        // TODO: Move this to the view layer
        if(reservationTitle == null) {
            return "Book Meeting Room";
        }

        return reservationTitle;
    }

    public void setReservationTitle(String reservationTitle) {
        this.reservationTitle = reservationTitle;
    }

    public String getCurrentTime() {
        return iso8601(currentTime);
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
        // TODO: Move this to the view layer
        if(reservationStart == null || reservationEnd == null) {
            return " ";
        }

        return String.format("%s - %s", iso8601(reservationStart), iso8601(reservationEnd));
    }

    public final String iso8601(Date dateTime) {
        if (dateTime == null) {
            return "";
        }
        return ISO8601.format(dateTime);
    }
}
