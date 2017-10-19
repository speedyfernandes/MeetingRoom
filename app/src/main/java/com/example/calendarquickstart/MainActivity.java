package com.example.calendarquickstart;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by miguel on 5/29/15.
 */

public class MainActivity extends Activity {
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final long ROOMLIST_REFRESH_PERIOD = 10 * 1000;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    /**
     * A Google Calendar API service object used to access the API.
     * Note: Do not confuse this class with API library's model classes, which
     * represent specific data structures.
     */
    com.google.api.services.calendar.Calendar mService;
    GoogleAccountCredential credential;
    private TextView txtRoom;
    private TextView txtStatus;
    private TextView txtTime;
    private TextView txtFirstDetail;
    private TextView txtSecondDetail;
    private TextView txtThirdDetail;
    private View vwContainer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        vwContainer = findViewById(R.id.vwContainer);
        txtRoom = (TextView) findViewById(R.id.txtRoom);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtThirdDetail = (TextView) findViewById(R.id.txtThirdDetail);
        txtSecondDetail = (TextView) findViewById(R.id.txtSecondDetail);
        txtFirstDetail = (TextView) findViewById(R.id.txtFirstDetail);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
            startAutoRefreshData();
        } else {
            showMessage("Google Play Services required: " +
                    "after installing, close and relaunch this app.");
        }
    }

    @Override
    public void onPause() {
        stopAutoRefreshData();
        super.onPause();
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    refreshResults();
                } else {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        refreshResults();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    showMessage("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    refreshResults();
                } else {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                Log.d("MainActivity", "Refresh data");
                new ApiAsyncTask(this).execute();
            } else {
                showMessage("No network connection available.");
            }
        }
    }

    /**
     * Update the screen with the given Reservation; called from
     * background threads and async tasks that need to update the UI (in the
     * UI thread).
     *
     * @param reservation a Reservation object to populate the screen with.
     */
    public void updateReservation(final Reservation reservation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (reservation == null) {
                    showMessage("Error retrieving data!");
                } else {

                    if (reservation.isBooked()) {
                        showBooked(reservation);
                    } else {
                        showFree(reservation);
                    }
                }
            }
        });
    }

    private void showFree(Reservation reservation) {
        vwContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.free));
        txtStatus.setTextColor(ContextCompat.getColor(this, R.color.booked));
        txtThirdDetail.setTextColor(ContextCompat.getColor(this, R.color.booked));

        txtRoom.setText(reservation.getReservationRoom());
        txtStatus.setText("Free");
        txtTime.setText(reservation.getCurrentTime());
        txtFirstDetail.setText(reservation.getReservationTime());
        txtSecondDetail.setText(reservation.getReservationTitle());

        String prefix = "Using";
        Spannable wordtoSpan = new SpannableString(String.format("%s %s", prefix,
                reservation.getReservationOwner()));
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE),
                0, prefix.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.booked)),
                prefix.length() + 1, wordtoSpan.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        txtThirdDetail.setText(wordtoSpan);
    }

    private void showBooked(Reservation reservation) {
        vwContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.booked));
        txtStatus.setTextColor(ContextCompat.getColor(this, R.color.free));
        txtThirdDetail.setTextColor(ContextCompat.getColor(this, R.color.free));

        txtRoom.setText(reservation.getReservationRoom());
        txtStatus.setText("Booked");
        txtTime.setText(reservation.getCurrentTime());
        txtFirstDetail.setText(reservation.getReservationTime());
        txtSecondDetail.setText(reservation.getReservationTitle());

        String prefix = "With";
        Spannable wordtoSpan = new SpannableString(String.format("%s %s", prefix,
                reservation.getReservationOwner()));
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE),
                0, prefix.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.free)),
                prefix.length() + 1, wordtoSpan.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        txtThirdDetail.setText(wordtoSpan);
    }

    /**
     * Show a status message in the list header TextView; called from background
     * threads and async tasks that need to update the UI (in the UI thread).
     *
     * @param message a String to display in the UI header TextView.
     */
    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMessage(message);
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    void showMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    final Handler handler = new Handler();

    final Runnable refreshDataRunnable = new Runnable() {
        @Override
        public void run() {
            refreshResults();
            startAutoRefreshData();
        }
    };

    private void startAutoRefreshData() {
        handler.postDelayed(refreshDataRunnable, ROOMLIST_REFRESH_PERIOD);
    }

    private void stopAutoRefreshData() {
        handler.removeCallbacks(refreshDataRunnable);
    }

}