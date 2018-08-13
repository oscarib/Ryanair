package com.ryanair.oif.interflights.controllers;

import com.ryanair.oif.interflights.domain.Flight;
import com.ryanair.oif.interflights.domain.Leg;
import com.ryanair.oif.interflights.util.RyanairCommons;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FlightController {

    @RequestMapping("/interconnections")
    public List<Flight> getFlights(
            @RequestParam(value="departure") String departureAirport,
            @RequestParam(value="arrival") String arrivalAirport,
            @RequestParam(value="departureDateTime") String departureDateTime,
            @RequestParam(value="arrivalDateTime") String arrivalDateTime){

        List<Flight> flights = new ArrayList<>();

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
        flights.add(flight);

        flight = new Flight();
        legs = new ArrayList<>();
        leg = new Leg();
        leg.setDepartureAirport(departureAirport);
        leg.setArrivalAirport("PAR");
        departureTime = RyanairCommons.getDateISO("2018", "08", "20", "20:00");
        arrivalTime = RyanairCommons.getDateISO("2018", "08", "20", "21:30");
        leg.setDepartureDateTime(departureTime);
        leg.setArrivalDateTime(arrivalTime);
        flight.setStops(1);
        legs.add(leg);

        leg = new Leg();
        leg.setDepartureAirport("PAR");
        leg.setArrivalAirport(arrivalAirport);
        departureTime = RyanairCommons.getDateISO("2018", "08", "20", "23:45");
        arrivalTime = RyanairCommons.getDateISO("2018", "08", "21", "06:00");
        leg.setDepartureDateTime(departureTime);
        leg.setArrivalDateTime(arrivalTime);
        flight.setStops(1);
        legs.add(leg);
        flight.setLegs(legs);
        flights.add(flight);

        return flights;
    }
}
