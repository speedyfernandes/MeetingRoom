package com.example.calendarquickstart;

/**
 * Created by Jerry on 19/10/17.
 */

public class Reservation {

    private boolean booked;
    private String currentTime;
    private String reservationRoom;
    private String reservationTime;
    private String reservationOwner;
    private String reservationTitle;

    Reservation() {
        booked = false;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getReservationRoom() {
        return reservationRoom;
    }

    public void setReservationRoom(String reservationRoom) {
        this.reservationRoom = reservationRoom;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getReservationOwner() {
        return reservationOwner;
    }

    public void setReservationOwner(String reservationOwner) {
        this.reservationOwner = reservationOwner;
    }

    public String getReservationTitle() {
        return reservationTitle;
    }

    public void setReservationTitle(String reservationTitle) {
        this.reservationTitle = reservationTitle;
    }
}
