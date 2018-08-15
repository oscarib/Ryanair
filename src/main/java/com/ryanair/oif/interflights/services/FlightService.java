package com.ryanair.oif.interflights.services;

import com.ryanair.oif.interflights.domain.Flight;

import java.util.List;

public interface FlightService {

    List<Flight> getFlights(String departureAirport, String arrivalAirport);
}
