package com.fyp.Beauticianatyourdoorstep.model;

import java.io.Serializable;

public interface BookingInterface extends Serializable {
    String getBookingId();

    String getCustomerEmail();

    String getBeauticianEmail();

    String getServiceName();

    String getServiceDetails();

    String getBookingStatus();

    String getBookingBeauticianStatus();

    String getBookingCustomerStatus();

    String getRequestDate();

    String getStartTime();

    String getEndTime();

    String getCancellationReason();

    String getServiceReview();

    Integer getServiceRating();

    void setBookingCustomerStatus(String bookingCustomerStatus);

    void setBookingStatus(String bookingStatus);

    void setServiceReview(String serviceReview);

    void setServiceRating(Integer serviceRating);
}
