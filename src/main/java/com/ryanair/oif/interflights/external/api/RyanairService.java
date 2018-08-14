package com.ryanair.oif.interflights.external.api;

import com.ryanair.oif.interflights.external.api.domain.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RyanairService {

    @Autowired
    RyanairAPI ryanairAPI;

    public List<Route> searchRoutesByDest(String IATACode){
        List<Route> routes = ryanairAPI.getRoutes();
        return searchRoutesByDest(routes, IATACode);
    }

    //This is supposed to be run in a java8 JVM only
    public List<Route> searchRoutesByDest(List<Route> routes, String IATACode){
        Stream<Route> routesCollection = routes.stream();
        Stream<Route> filteredRoutesCollection = routesCollection.filter(item -> item.getAirportTo().equals(IATACode));
        return filteredRoutesCollection.collect(Collectors.toList());
    }

    //This is supposed to be run in a java8 JVM only
    public List<Route> searchRoutesByDep(List<Route> routes, String IATACode){
        Stream<Route> routesCollection = routes.stream();
        Stream<Route> filteredRoutesCollection = routesCollection.filter(item -> item.getAirportFrom().equals(IATACode));
        return filteredRoutesCollection.collect(Collectors.toList());
    }
}
