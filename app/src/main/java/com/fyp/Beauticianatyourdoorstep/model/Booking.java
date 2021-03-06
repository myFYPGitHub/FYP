package com.fyp.Beauticianatyourdoorstep.model;

public class Booking implements BookingInterface {
    private String bookingId, customerEmail, beauticianEmail, serviceName, serviceDetails, bookingBeauticianStatus, bookingCustomerStatus, bookingStatus, requestDate, startTime, endTime, cancellationReason, serviceReview;
    private Integer serviceRating;

    public Booking() {
        //Required Constructor for Firebase
    }

    public Booking(String bookingId, String customerEmail, String beauticianEmail, String serviceName, String serviceDetails, String requestDate, String startTime, String endTime) {
        this.bookingId = bookingId;
        this.customerEmail = customerEmail;
        this.beauticianEmail = beauticianEmail;
        this.serviceName = serviceName;
        this.serviceDetails = serviceDetails;
        this.bookingBeauticianStatus = "pending";
        this.bookingCustomerStatus = "pending";
        this.bookingStatus = "pending";
        this.requestDate = requestDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceRating = 0;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getBeauticianEmail() {
        return beauticianEmail;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceDetails() {
        return serviceDetails;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getBookingBeauticianStatus() {
        return bookingBeauticianStatus;
    }

    public String getBookingCustomerStatus() {
        return bookingCustomerStatus;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public String getServiceReview() {
        return serviceReview;
    }

    public Integer getServiceRating() {
        return serviceRating;
    }

    public void setBookingCustomerStatus(String bookingCustomerStatus) {
        this.bookingCustomerStatus = bookingCustomerStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void setServiceReview(String serviceReview) {
        this.serviceReview = serviceReview;
    }

    public void setServiceRating(Integer serviceRating) {
        this.serviceRating = serviceRating;
    }
}
