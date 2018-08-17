package com.ryanair.oif.routes.external.api.domain;

public class Route {

    private String airportFrom;
    private String airportTo;
    private String connectingAirport;
    private String newRoute;
    private String seasonalRoute;
    private String group;

    public String getAirportFrom() {
        return airportFrom;
    }

    public void setAirportFrom(String airportFrom) {
        this.airportFrom = airportFrom;
    }

    public String getAirportTo() {
        return airportTo;
    }

    public void setAirportTo(String airportTo) {
        this.airportTo = airportTo;
    }

    public String getConnectingAirport() {
        return connectingAirport;
    }

    public void setConnectingAirport(String connectingAirport) {
        this.connectingAirport = connectingAirport;
    }

    public String getNewRoute() {
        return newRoute;
    }

    public void setNewRoute(String newRoute) {
        this.newRoute = newRoute;
    }

    public String getSeasonalRoute() {
        return seasonalRoute;
    }

    public void setSeasonalRoute(String seasonalRoute) {
        this.seasonalRoute = seasonalRoute;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
