package com.fyp.Beauticianatyourdoorstep.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BeauticianItem implements Serializable {
    private Beautician beautician;
    private ArrayList<BeauticianService> services;

    public BeauticianItem(Beautician beautician, ArrayList<BeauticianService> services) {
        this.beautician = beautician;
        this.services = services;
    }

    public Beautician getBeauticianInstance() {
        return beautician;
    }

    public ArrayList<BeauticianService> getServices() {
        return services;
    }
}
