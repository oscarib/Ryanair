package com.ryanair.oif.interflights.external.api;

import com.ryanair.oif.interflights.external.api.domain.MonthFlights;
import com.ryanair.oif.interflights.external.api.domain.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RyanairService {

    private final RyanairAPI ryanairAPI;

    @Autowired
    public RyanairService(RyanairAPI ryanairAPI) {
        this.ryanairAPI = ryanairAPI;
    }

    public MonthFlights getMonthFlights(String departureAirport, String arrivalAirport, String year, String month){
        return ryanairAPI.getMonthSchedules(departureAirport, arrivalAirport, year, month);
    }

    public List<Route> searchRoutesByDest(String IATACode){
        List<Route> routes = ryanairAPI.getRoutes();
        return searchRoutesByDest(routes, IATACode);
    }

    //This is supposed to be run in a java8 JVM only
    public List<Route> searchRoutesByDep(List<Route> routes, String IATACode){
        Stream<Route> routesCollection = routes.stream();
        Stream<Route> filteredRoutesCollection;
        filteredRoutesCollection = routesCollection.filter(route -> route.getAirportFrom().equals(IATACode));
        return filteredRoutesCollection.collect(Collectors.toList());
    }

    //This is supposed to be run in a java8 JVM only
    public List<Route> filterOutDepartureAirport(List<Route> routes, String IATACode){
        Stream<Route> routesCollection = routes.stream();
        Stream<Route> filteredRoutesCollection;
        filteredRoutesCollection = routesCollection.filter(route -> !route.getAirportFrom().equals(IATACode));
        return filteredRoutesCollection.collect(Collectors.toList());
    }

    //This is supposed to be run in a java8 JVM only
    private List<Route> searchRoutesByDest(List<Route> routes, String IATACode){
        Stream<Route> routesCollection = routes.stream();
        Stream<Route> filteredRoutesCollection = routesCollection.filter(route -> {
            boolean included = route.getAirportTo().equals(IATACode);
            boolean excluded = route.getConnectingAirport() == null;
            return included && excluded;
        });
        return filteredRoutesCollection.collect(Collectors.toList());
    }
}
