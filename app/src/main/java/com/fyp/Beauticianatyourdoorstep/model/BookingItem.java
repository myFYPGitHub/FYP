package com.fyp.Beauticianatyourdoorstep.model;

import java.io.Serializable;

public class BookingItem implements Serializable {
    private final Beautician beautician;
    private final Booking booking;

    public BookingItem(Beautician beautician, Booking booking) {
        this.beautician = beautician;
        this.booking = booking;
    }

    public Beautician getBeauticianInstance() {
        return beautician;
    }

    public Booking getBookingInstance() {
        return booking;
    }
}
