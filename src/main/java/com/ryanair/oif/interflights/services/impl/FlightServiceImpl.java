package com.ryanair.oif.interflights.services.impl;

import com.ryanair.oif.interflights.external.api.RyanairService;
import com.ryanair.oif.interflights.external.api.domain.Route;
import com.ryanair.oif.interflights.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    RyanairService ryanairService;

    @Override
    public List<Route> searchRoutesByDest(String IATACode) {
        return ryanairService.searchRoutesByDest(IATACode);
    }
}
