package com.ryanair.oif.interflights.services.impl;

import com.ryanair.oif.interflights.domain.Leg;
import com.ryanair.oif.interflights.domain.RouteFlight;
import com.ryanair.oif.interflights.external.api.RyanairService;
import com.ryanair.oif.interflights.external.api.domain.DayFlights;
import com.ryanair.oif.interflights.external.api.domain.Flight;
import com.ryanair.oif.interflights.external.api.domain.MonthFlights;
import com.ryanair.oif.interflights.external.api.domain.Route;
import com.ryanair.oif.interflights.services.FlightService;
import com.ryanair.oif.interflights.util.RyanairCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FlightServiceImpl implements FlightService {

    private Logger logger = LoggerFactory.getLogger(FlightService.class);

    private final RyanairService ryanairService;

    private List<Route> routes2Dest;

    @Autowired
    public FlightServiceImpl(RyanairService ryanairService) {
        this.ryanairService = ryanairService;
    }

    @Override
    public List<RouteFlight> getFlights(String departureAirport, String arrivalAirport,
                                        String departureDateTimeISO, String arrivalDateTimeISO){

        String day = RyanairCommons.getDay(departureDateTimeISO);
        String month = RyanairCommons.getMonth(departureDateTimeISO);
        String year = RyanairCommons.getYear(departureDateTimeISO);
        Date requestedDepartureDate = RyanairCommons.parse2Date(departureDateTimeISO);
        Date requestedArrivalDate = RyanairCommons.parse2Date(arrivalDateTimeISO);

        //Getting all direct and indirect routes
        List<RouteFlight> routeFlights = new ArrayList<>();
        routeFlights.addAll(getDirectRoutes(departureAirport, arrivalAirport));
        routeFlights.addAll(getIndirectRoutes(departureAirport));

        //Routes validation
        List<RouteFlight> flights2Remove = new ArrayList<>();
        for (RouteFlight routeFlight : routeFlights) {

            checkRouteWithinTimeFrame(
                    day, month, year, requestedDepartureDate, requestedArrivalDate, flights2Remove, routeFlight
            );
            checkRouteInterconnections(flights2Remove, routeFlight);

        }

        routeFlights.removeAll(flights2Remove);

        return routeFlights;
    }

    //Check if indirect flights gives at least 2h timelapse between first flight arrival and second flight departure
    private void checkRouteInterconnections(List<RouteFlight> flights2Remove, RouteFlight routeFlight) {

        //TODO: Take into consideration more than one valid flight per schedule.
        //      A schedule could contain two flights, from same dep an arr airports,
        //      at different times. Theoretically, they both could be within requested time frame,
        //      Hence, those flights should be duplicated as valid alternatives separately

        if (routeFlight.getLegs().size()>1 && !flights2Remove.contains(routeFlight)) {

            Leg leg1 = routeFlight.getLegs().get(0);
            Leg leg2 = routeFlight.getLegs().get(1);
            Date leg2Departure = RyanairCommons.parse2Date(leg2.getDepartureDateTime());
            Date leg1Arrival = RyanairCommons.parse2Date(leg1.getArrivalDateTime());

            if (leg2Departure==null || leg1Arrival==null) {
                flights2Remove.add(routeFlight);
                logger.info("Route is a two legs flight, but there were somehow of a problem trying to get times");
                return;
            }

            long diff = leg2Departure.getTime() - leg1Arrival.getTime();
            long diffHours = diff / (60 * 60 * 1000);
            if (diffHours<0){
                flights2Remove.add(routeFlight);
                logger.info("Route is a two legs flight, but second flight starts earlier than first flight arrival");
            } else {
                logger.info("Route is a two legs flight. Time between first flight arrival " +
                        "and second flight departure is higher than " + diffHours + "h");
                if (diffHours<2) {
                    flights2Remove.add(routeFlight);
                }
            }
        }
    }

    //Checks that flights departure and arrival times are within requested frames
    private void checkRouteWithinTimeFrame(String day, String month, String year, Date requestedDepartureDate,
                                           Date requestedArrivalDate, List<RouteFlight> flights2Remove,
                                           RouteFlight routeFlight) {

        for (Leg leg : routeFlight.getLegs()) {

            if (!flights2Remove.contains(routeFlight)) {
                String legDeparture = leg.getDepartureAirport();
                String legArrival = leg.getArrivalAirport();

                List<DayFlights> daySchedules = getDayScheduledFlights(
                        day, month, year, flights2Remove, routeFlight, legDeparture, legArrival
                );

                if (daySchedules == null){
                    continue;
                }

                checkLegWithinTimeFrame(
                        day, month, year, requestedDepartureDate, requestedArrivalDate,
                        flights2Remove, routeFlight, daySchedules, leg
                );
            }
        }
    }

    private List<DayFlights> getDayScheduledFlights(String day, String month, String year,
                                                    List<RouteFlight> flights2Remove, RouteFlight routeFlight,
                                                    String legDeparture, String legArrival) {

        //Get the month Schedules for the leg
        MonthFlights monthFlights = ryanairService.getMonthFlights(legDeparture, legArrival, year, month);
        logger.info("Requesting schedules for flight '"+legDeparture+"'-->'"+legArrival+"' " +
                     "on year '"+year+"' and month '"+month+"'");

        if (monthFlights.getMonth()==null) {
            //No Flights found for the leg in the whole month
            flights2Remove.add(routeFlight);
            return null;
        }

        //Get the day Schedules for the leg
        List<DayFlights> monthSchedules = monthFlights.getDays();
        List<DayFlights> daySchedules = filterSchedulesByDay(day, monthSchedules);
        if (daySchedules.size()==0){
            //No Flights found for the leg in the whole day
            flights2Remove.add(routeFlight);
            return null;
        }

        return daySchedules;
    }

    private void checkLegWithinTimeFrame(String day, String month, String year, Date requestedDepartureDate,
                                         Date requestedArrivalDate, List<RouteFlight> flights2Remove,
                                         RouteFlight routeFlight, List<DayFlights> daySchedules,
                                         Leg legFlight) {

        int matches = 0;

        for (DayFlights dayFlights : daySchedules) {
            List<Flight> flights = dayFlights.getFlights();
            String departureTime = "";
            String arrivalTime = "";
            for (Flight flight : flights) {
                departureTime = flight.getDepartureTime();
                arrivalTime = flight.getArrivalTime();
                Date departureFlightDate = RyanairCommons.parse2Date(year, month, day, departureTime);
                Date arrivalFlightDate = RyanairCommons.parse2Date(year, month, day, arrivalTime);
                boolean flightStartsAfterRequested = requestedDepartureDate.compareTo(departureFlightDate)<=0;
                boolean flightArrivesBeforeRequested = requestedArrivalDate.compareTo(arrivalFlightDate)>=0;
                if (flightStartsAfterRequested && flightArrivesBeforeRequested) {
                    matches++;
                }
            }
            if (matches==0) {
                flights2Remove.add(routeFlight);
            } else {
                String departureDateTime = RyanairCommons.getDateISOAsTxt(year, month, day, departureTime);
                String arrivalDateTime = RyanairCommons.getDateISOAsTxt(year, month, day, arrivalTime);
                legFlight.setDepartureDateTime(departureDateTime);
                legFlight.setArrivalDateTime(arrivalDateTime);
            }
        }
    }

    //This is supposed to be run in a java8 JDK only
    private List<DayFlights> filterSchedulesByDay(String day, List<DayFlights> monthFlights) {
        List<DayFlights> filteredFlights;Stream<DayFlights> monthFlightsCollection = monthFlights.stream();
        Stream<DayFlights> filteredDayFlights;
        filteredDayFlights = monthFlightsCollection.filter(monthFlight -> monthFlight.getDay()==Integer.parseInt(day));
        filteredFlights = filteredDayFlights.collect(Collectors.toList());
        return filteredFlights;
    }

    private List<RouteFlight> getDirectRoutes(String departureAirport, String arrivalAirport){

        routes2Dest = ryanairService.searchRoutesByDest(arrivalAirport);
        List<Route> directRoutes = ryanairService.searchRoutesByDep(routes2Dest, departureAirport);

        List<RouteFlight> directRouteFlights = new ArrayList<>();

        for (Route directRoute : directRoutes) {
            String airportFrom = directRoute.getAirportFrom();
            String airportTo = directRoute.getAirportTo();
            RouteFlight directRouteFlight = getDirectRoute(airportFrom, airportTo);
            directRouteFlights.add(directRouteFlight);
        }

        return directRouteFlights;
    }

    private List<RouteFlight> getIndirectRoutes(String departureAirport){

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

    private RouteFlight getDirectRoute(String departureAirport, String arrivalAirport){

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
