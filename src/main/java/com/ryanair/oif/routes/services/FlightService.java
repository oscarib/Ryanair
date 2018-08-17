package com.ryanair.oif.routes.services;

import com.ryanair.oif.routes.domain.RouteFlight;

import java.util.List;

public interface FlightService {

    List<RouteFlight> getFlights(String departureAirport, String arrivalAirport,
                                 String departureDateTime, String arrivalDateTime);
}
