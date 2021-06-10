package com.fyp.Beauticianatyourdoorstep.model;

import java.io.Serializable;

public class BeauticianService implements Serializable {
    private String serviceName;
    private Double servicePrice;
    private Boolean serviceAvailability;

    public BeauticianService(){
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

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(Double servicePrice) {
        this.servicePrice = servicePrice;
    }

    public Boolean getServiceAvailability() {
        return serviceAvailability;
    }

    public void setServiceAvailability(Boolean serviceAvailability) {
        this.serviceAvailability = serviceAvailability;
    }
}
