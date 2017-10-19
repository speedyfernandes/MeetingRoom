package com.example.calendarquickstart;

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */

/**
 * Created by miguel on 5/29/15.
 */

public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;

    /**
     * Constructor.
     *
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.updateReservation(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);

        } catch (IOException e) {
            mActivity.updateStatus("The following error occurred: " +
                    e.getMessage());
        }
        return null;
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     *
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private Reservation getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.

        //CalendarList list = mActivity.mService.calendarList().list().execute();

        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime fetchFrom = new DateTime(now.getValue() - 60 * 1000 * 60 * 12);
        Events events = mActivity.mService.events().list("fluxfederation.com_2d35333437393138333334@resource.calendar.google.com")
                .setMaxResults(10)
                .setTimeMin(fetchFrom)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        Reservation reservation = new Reservation(new Date(now.getValue()));

        for (Event event : items) {
            Date start = new Date(event.getStart().getDateTime().getValue());
            Date end = new Date(event.getEnd().getDateTime().getValue());
            Date current = new Date(now.getValue());
            if (start.before(current) && end.after(current)) {
                reservation.setBooked(true);
                reservation.setReservationRoom(event.getLocation());
                reservation.setReservationOwner(event.getOrganizer());
                reservation.setReservationTitle(event.getSummary());
                reservation.setReservationStart(start);
                reservation.setReservationEnd(end);
            }
        }
        return reservation;
    }

}