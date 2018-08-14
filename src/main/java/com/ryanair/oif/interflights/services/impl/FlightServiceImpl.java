package com.ryanair.oif.interflights.services.impl;

import com.ryanair.oif.interflights.domain.Flight;
import com.ryanair.oif.interflights.domain.Leg;
import com.ryanair.oif.interflights.external.api.RyanairService;
import com.ryanair.oif.interflights.external.api.domain.Route;
import com.ryanair.oif.interflights.services.FlightService;
import com.ryanair.oif.interflights.util.RyanairCommons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    RyanairService ryanairService;

    @Override
    public List<Flight> getDirectFlights(String departureAirport, String arrivalAirport){

        List<Route> routes2Dest = ryanairService.searchRoutesByDest(arrivalAirport);
        List<Route> directRoutes = ryanairService.searchRoutesByDep(routes2Dest, departureAirport);

        List<Flight> directFlights = new ArrayList<>();

        for (Route directRoute : directRoutes) {
            directFlights.add(getDirectFlight(directRoute.getAirportFrom(), directRoute.getAirportTo()));
        }

        return directFlights;
    }

    private Flight getDirectFlight(String departureAirport, String arrivalAirport){

        Flight flight = new Flight();
        List<Leg> legs = new ArrayList<>();
        Leg leg = new Leg();
        leg.setDepartureAirport(departureAirport);
        leg.setArrivalAirport(arrivalAirport);
        String departureTime = RyanairCommons.getDateISO("2018", "08", "20", "20:00");
        String arrivalTime = RyanairCommons.getDateISO("2018", "08", "21", "04:00");
        leg.setDepartureDateTime(departureTime);
        leg.setArrivalDateTime(arrivalTime);
        flight.setStops(0);
        legs.add(leg);
        flight.setLegs(legs);
        return flight;
    }
}
