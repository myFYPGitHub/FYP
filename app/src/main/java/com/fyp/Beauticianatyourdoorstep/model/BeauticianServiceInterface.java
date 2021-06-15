package com.fyp.Beauticianatyourdoorstep.model;

import java.io.Serializable;

public interface BeauticianServiceInterface extends Serializable {

    String getServiceName();

    Double getServicePrice();

    Boolean getServiceAvailability() ;
}
