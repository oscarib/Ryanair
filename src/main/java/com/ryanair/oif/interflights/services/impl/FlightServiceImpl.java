package com.ryanair.oif.interflights.services.impl;

import com.ryanair.oif.interflights.domain.RouteFlight;
import com.ryanair.oif.interflights.domain.Leg;
import com.ryanair.oif.interflights.external.api.RyanairService;
import com.ryanair.oif.interflights.external.api.domain.DayFlights;
import com.ryanair.oif.interflights.external.api.domain.Flight;
import com.ryanair.oif.interflights.external.api.domain.MonthFlights;
import com.ryanair.oif.interflights.external.api.domain.Route;
import com.ryanair.oif.interflights.services.FlightService;
import com.ryanair.oif.interflights.util.RyanairCommons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FlightServiceImpl implements FlightService {

    private final RyanairService ryanairService;

    private List<Route> routes2Dest;

    @Autowired
    public FlightServiceImpl(RyanairService ryanairService) {
        this.ryanairService = ryanairService;
    }

    @Override
    public List<RouteFlight> getFlights(String departureAirport, String arrivalAirport,
                                        String departureDateTimeISO, String arrivalDateTimeISO){

        List<RouteFlight> routeFlights = new ArrayList<>();

        routeFlights.addAll(getDirectFlights(departureAirport, arrivalAirport));
        routeFlights.addAll(getIndirectFlights(departureAirport));

        String day = RyanairCommons.getDay(departureDateTimeISO);
        String month = RyanairCommons.getMonth(departureDateTimeISO);
        String year = RyanairCommons.getYear(departureDateTimeISO);
        Date departureDate = RyanairCommons.parse2Date(departureDateTimeISO);
        Date arrivalDate = RyanairCommons.parse2Date(arrivalDateTimeISO);

        //Checking schedule availability
        List<RouteFlight> flights2Remove = new ArrayList<>();
        for (RouteFlight routeFlight : routeFlights) {

            //Get the Schedules for the routeFlights on the month
            for (Leg legFlight : routeFlight.getLegs()) {

                String legDeparture = legFlight.getDepartureAirport();
                String legArrival = legFlight.getArrivalAirport();

                MonthFlights monthFlights = ryanairService.getMonthFlights(legDeparture, legArrival, year, month);
                if (monthFlights.getMonth()==null) {
                    //No Flights found for provided month
                    flights2Remove.add(routeFlight);
                    continue;
                }

                //Get the Schedules for the routeFlights on the day
                List<DayFlights> monthSchedules = monthFlights.getDays();
                List<DayFlights> daySchedules = filterSchedulesByDay(day, monthSchedules);
                if (daySchedules.size()==0){
                    //No Flights found for provided day
                    flights2Remove.add(routeFlight);
                    continue;
                }

                //Checking flight dates are not earlier than requested departureDate nor later than requested arrivalDate
                for (DayFlights dayFlights : daySchedules) {
                    List<Flight> flights = dayFlights.getFlights();
                    int matches = 0;
                    for (Flight flight : flights) {
                        Date departureFlightDate = RyanairCommons.parse2Date(year, month, day, flight.getDepartureTime());
                        Date arrivalFlightDate = RyanairCommons.parse2Date(year, month, day, flight.getArrivalTime());
                        boolean flightStartsAfterRequested = departureDate.compareTo(departureFlightDate)<=0;
                        boolean flightArrivesBeforeRequested = arrivalDate.compareTo(arrivalFlightDate)>=0;
                        if (flightStartsAfterRequested && flightArrivesBeforeRequested) {
                            matches++;
                        }
                    }
                    if (matches==0) {
                        flights2Remove.add(routeFlight);
                    }
                }
            }
        }

        routeFlights.removeAll(flights2Remove);

        //TODO: Check out interconnection time frames

        return routeFlights;
    }

    private List<DayFlights> filterSchedulesByDay(String day, List<DayFlights> monthFlights) {
        List<DayFlights> filteredFlights;Stream<DayFlights> monthFlightsCollection = monthFlights.stream();
        Stream<DayFlights> filteredDayFlights;
        filteredDayFlights = monthFlightsCollection.filter(monthFlight -> monthFlight.getDay()==Integer.parseInt(day));
        filteredFlights = filteredDayFlights.collect(Collectors.toList());
        return filteredFlights;
    }

    private List<RouteFlight> getDirectFlights(String departureAirport, String arrivalAirport){

        routes2Dest = ryanairService.searchRoutesByDest(arrivalAirport);
        List<Route> directRoutes = ryanairService.searchRoutesByDep(routes2Dest, departureAirport);

        List<RouteFlight> directRouteFlights = new ArrayList<>();

        for (Route directRoute : directRoutes) {
            String airportFrom = directRoute.getAirportFrom();
            String airportTo = directRoute.getAirportTo();
            RouteFlight directRouteFlight = getDirectFlight(airportFrom, airportTo);
            directRouteFlights.add(directRouteFlight);
        }

        return directRouteFlights;
    }

    private List<RouteFlight> getIndirectFlights(String departureAirport){

        List<RouteFlight> indirectRouteFlights = new ArrayList<>();

        List<Route> secondLegRoutes = ryanairService.filterOutDepartureAirport(routes2Dest, departureAirport);
        for (Route secondLegRoute : secondLegRoutes) {
            RouteFlight routeFlight = new RouteFlight();
            List<Leg> legs = new ArrayList<>();
            routeFlight.setStops(1);
            legs.add(getDefaultLeg(departureAirport, secondLegRoute.getAirportFrom()));
            legs.add(getDefaultLeg(secondLegRoute.getAirportFrom(), secondLegRoute.getAirportTo()));
            routeFlight.setLegs(legs);
            indirectRouteFlights.add(routeFlight);
        }

        return indirectRouteFlights;
    }

    private RouteFlight getDirectFlight(String departureAirport, String arrivalAirport){

        RouteFlight routeFlight = new RouteFlight();
        List<Leg> legs = new ArrayList<>();
        routeFlight.setStops(0);
        legs.add(getDefaultLeg(departureAirport, arrivalAirport));
        routeFlight.setLegs(legs);
        return routeFlight;
    }

    private Leg getDefaultLeg(String departureAirport, String arrivalAirport){
        Leg leg = new Leg();
        leg.setDepartureAirport(departureAirport);
        leg.setArrivalAirport(arrivalAirport);
        String departureTime = RyanairCommons.getDateISOAsTxt("0000", "0", "00", "00:00");
        String arrivalTime = RyanairCommons.getDateISOAsTxt("0000", "0", "00", "00:00");
        leg.setDepartureDateTime(departureTime);
        leg.setArrivalDateTime(arrivalTime);
        return leg;
    }
}
