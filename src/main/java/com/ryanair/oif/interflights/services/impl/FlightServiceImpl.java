package com.ryanair.oif.interflights.services.impl;

import com.ryanair.oif.interflights.domain.Flight;
import com.ryanair.oif.interflights.domain.Leg;
import com.ryanair.oif.interflights.external.api.RyanairService;
import com.ryanair.oif.interflights.external.api.domain.Route;
import com.ryanair.oif.interflights.services.FlightService;
import com.ryanair.oif.interflights.util.RyanairCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final RyanairService ryanairService;

    private List<Route> routes2Dest;

    @Autowired
    public FlightServiceImpl(RyanairService ryanairService) {
        this.ryanairService = ryanairService;
    }

    @Override
    public List<Flight> getFlights(String departureAirport, String arrivalAirport){
        List<Flight> flights = new ArrayList<>(getDirectFlights(departureAirport, arrivalAirport));
        flights.addAll(getInterconnectedFlights(departureAirport));
        return flights;
    }

    private List<Flight> getDirectFlights(String departureAirport, String arrivalAirport){

        routes2Dest = ryanairService.searchRoutesByDest(arrivalAirport);
        List<Route> directRoutes = ryanairService.searchRoutesByDep(routes2Dest, departureAirport);

        List<Flight> directFlights = new ArrayList<>();

        for (Route directRoute : directRoutes) {
            String airportFrom = directRoute.getAirportFrom();
            String airportTo = directRoute.getAirportTo();
            Flight directFlight = getDirectFlight(airportFrom, airportTo);
            directFlights.add(directFlight);
        }

        return directFlights;
    }

    private List<Flight> getInterconnectedFlights(String departureAirport){

        List<Flight> indirectFlights = new ArrayList<>();

        List<Route> secondLegRoutes = ryanairService.filterOutDepartureAirport(routes2Dest, departureAirport);
        for (Route secondLegRoute : secondLegRoutes) {
            Flight flight = new Flight();
            List<Leg> legs = new ArrayList<>();
            flight.setStops(1);
            legs.add(getDefaultLeg(departureAirport, secondLegRoute.getAirportFrom()));
            legs.add(getDefaultLeg(secondLegRoute.getAirportFrom(), secondLegRoute.getAirportTo()));
            flight.setLegs(legs);
            indirectFlights.add(flight);
        }

        return indirectFlights;
    }

    private Flight getDirectFlight(String departureAirport, String arrivalAirport){

        Flight flight = new Flight();
        List<Leg> legs = new ArrayList<>();
        flight.setStops(0);
        legs.add(getDefaultLeg(departureAirport, arrivalAirport));
        flight.setLegs(legs);
        return flight;
    }

    private Leg getDefaultLeg(String departureAirport, String arrivalAirport){
        Leg leg = new Leg();
        leg.setDepartureAirport(departureAirport);
        leg.setArrivalAirport(arrivalAirport);
        String departureTime = RyanairCommons.getDateISO("0000", "0", "00", "00:00");
        String arrivalTime = RyanairCommons.getDateISO("0000", "0", "00", "00:00");
        leg.setDepartureDateTime(departureTime);
        leg.setArrivalDateTime(arrivalTime);
        return leg;
    }
}
