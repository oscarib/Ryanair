package com.ryanair.oif.routes.controllers;

import com.ryanair.oif.routes.domain.RouteFlight;
import com.ryanair.oif.routes.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @RequestMapping("/interconnections")
    public List<RouteFlight> getFlights(
            @RequestParam(value="departure") String departureAirport,
            @RequestParam(value="arrival") String arrivalAirport,
            @RequestParam(value="departureDateTime") String departureDateTime,
            @RequestParam(value="arrivalDateTime") String arrivalDateTime){

        return flightService.getFlights(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime);
    }
}
