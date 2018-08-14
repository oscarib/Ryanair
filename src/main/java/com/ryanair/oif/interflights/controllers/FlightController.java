package com.ryanair.oif.interflights.controllers;

import com.ryanair.oif.interflights.domain.Flight;
import com.ryanair.oif.interflights.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlightController {

    @Autowired
    FlightService flightService;

    @RequestMapping("/interconnections")
    public List<Flight> getFlights(
            @RequestParam(value="departure") String departureAirport,
            @RequestParam(value="arrival") String arrivalAirport,
            @RequestParam(value="departureDateTime") String departureDateTime,
            @RequestParam(value="arrivalDateTime") String arrivalDateTime){

        return flightService.getDirectFlights(departureAirport, arrivalAirport);
    }
}
