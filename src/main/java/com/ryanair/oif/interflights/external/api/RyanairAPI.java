package com.ryanair.oif.interflights.external.api;

import com.ryanair.oif.interflights.external.api.domain.MonthFlights;
import com.ryanair.oif.interflights.external.api.domain.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RyanairAPI {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${ryanair.api.routesURL}")
    String routesEndpoint;

    @Value("${ryanair.api.schedulesURL}")
    String schedulesEndpoint;

    List<Route> getRoutes(){
        ResponseEntity<Route[]> responseEntity = restTemplate.getForEntity(routesEndpoint, Route[].class);
        Object[] routeObjects = responseEntity.getBody();
        if (routeObjects!=null) {
            return getRoutes(routeObjects);
        } else {
            return null;
        }
    }

    MonthFlights getMonthSchedules(String departureAirport, String arrivalAirport, String year, String month){
        String finalEndpoint = schedulesEndpoint + departureAirport + "/" + arrivalAirport;
        finalEndpoint += "/years/" + year + "/months/" + month;
        ResponseEntity<MonthFlights> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(finalEndpoint, MonthFlights.class);
            return responseEntity.getBody();

        } catch (HttpClientErrorException e) {
            //No flights found. Returning an empty response.
            return new MonthFlights();
        }
    }

    private List<Route> getRoutes(Object[] routeObjects) {
        List<Route> routes = new ArrayList<>();
        for (Object object : routeObjects) {
            Route route = (Route)object;
            routes.add(route);
        }
        return routes;
    }
}
