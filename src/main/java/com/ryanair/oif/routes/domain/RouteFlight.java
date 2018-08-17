package com.ryanair.oif.routes.domain;

import java.util.ArrayList;
import java.util.List;

public class RouteFlight {

    private int stops;
    private List<Leg> legs;

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public void addLeg(Leg leg){

        if (legs==null) {
            legs = new ArrayList<>();
        }

        legs.add(leg);
    }
}
