package com.example.calendarquickstart;

import android.content.Context;
import android.graphics.Color;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Jerry on 20/10/17.
 */

public class RoomStatusView extends RelativeLayout {

    private TextView txtRoom;
    private TextView txtStatus;
    private TextView txtTime;
    private TextView txtFirstDetail;
    private TextView txtSecondDetail;
    private TextView txtThirdDetail;
    private ImageView imgLogo;
    private Context context;

    public RoomStatusView(Context context) {
        super(context);
        this.context = context;
    }

    public RoomStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtRoom = (TextView) findViewById(R.id.txtRoom);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtThirdDetail = (TextView) findViewById(R.id.txtThirdDetail);
        txtSecondDetail = (TextView) findViewById(R.id.txtSecondDetail);
        txtFirstDetail = (TextView) findViewById(R.id.txtFirstDetail);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
    }

    public void updateStatus(Reservation reservation) {
        if (reservation.isBooked()) {
            showBooked(reservation);
        } else {
            showFree(reservation);
        }
    }

    private void showFree(Reservation reservation) {
        setBackgroundColor(ContextCompat.getColor(context, R.color.free));
        txtStatus.setTextColor(ContextCompat.getColor(context, R.color.booked));
        txtThirdDetail.setTextColor(ContextCompat.getColor(context, R.color.booked));

        txtRoom.setText(reservation.getReservationRoom());
        txtStatus.setText(context.getString(R.string.free));
        txtTime.setText(reservation.getCurrentTime());
        txtFirstDetail.setText("");
        txtSecondDetail.setText(context.getString(R.string.book_meeting_room));

        String prefix = context.getString(R.string.using);
        Spannable wordtoSpan = new SpannableString(String.format("%s %s", prefix,
                context.getString(R.string.google_calendars)));
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE),
                0, prefix.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.booked)),
                prefix.length() + 1, wordtoSpan.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        txtThirdDetail.setText(wordtoSpan);

        imgLogo.setImageDrawable(VectorDrawableCompat.create(getResources(),
                R.drawable.ic_flux_logo_free, context.getTheme()));
    }

    private void showBooked(Reservation reservation) {
        setBackgroundColor(ContextCompat.getColor(context, R.color.booked));
        txtStatus.setTextColor(ContextCompat.getColor(context, R.color.free));
        txtThirdDetail.setTextColor(ContextCompat.getColor(context, R.color.free));

        txtRoom.setText(reservation.getReservationRoom());
        txtStatus.setText(context.getString(R.string.booked));
        txtTime.setText(reservation.getCurrentTime());
        txtFirstDetail.setText(reservation.getReservationTime());
        txtSecondDetail.setText(reservation.getReservationTitle());

        String prefix = context.getString(R.string.with);
        Spannable wordtoSpan = new SpannableString(String.format("%s %s", prefix,
                reservation.getReservationOwner()));
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE),
                0, prefix.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.free)),
                prefix.length() + 1, wordtoSpan.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        txtThirdDetail.setText(wordtoSpan);

        imgLogo.setImageDrawable(VectorDrawableCompat.create(getResources(),
                R.drawable.ic_flux_logo_booked, context.getTheme()));
    }
}
