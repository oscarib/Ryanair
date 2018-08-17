package com.ryanair.oif.routes.external.api.domain;

import java.util.List;

public class DayFlights {

    private int day;
    private List<Flight> flights;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
