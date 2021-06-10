package com.fyp.Beauticianatyourdoorstep.model;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private final User user;
    private final Booking booking;

    public OrderItem(User user, Booking booking) {
        this.user = user;
        this.booking = booking;
    }

    public User getUserInstance() {
        return user;
    }

    public Booking getBookingInstance() {
        return booking;
    }
}
