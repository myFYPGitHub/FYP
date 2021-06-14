package com.fyp.Beauticianatyourdoorstep.model;

public class BeauticianService implements BeauticianServiceInterface {
    private String serviceName;
    private Double servicePrice;
    private Boolean serviceAvailability;

    public BeauticianService() {
        //Required Constructor for Firebase
    }

    public BeauticianService(String serviceName, Double servicePrice, Boolean serviceAvailability) {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceAvailability = serviceAvailability;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Double getServicePrice() {
        return servicePrice;
    }

    public Boolean getServiceAvailability() {
        return serviceAvailability;
    }
}
