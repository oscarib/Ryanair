package com.ryanair.oif.interflights.services;

import com.ryanair.oif.interflights.domain.RouteFlight;

import java.util.List;

public interface FlightService {

    List<RouteFlight> getFlights(String departureAirport, String arrivalAirport,
                                 String departureDateTime, String arrivalDateTime);
}
