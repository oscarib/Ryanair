package com.ryanair.oif.interflights.services;

import com.ryanair.oif.interflights.external.api.domain.Route;

import java.util.List;

public interface FlightService {

    List<Route> searchRoutesByDest(String IATACode);
}
