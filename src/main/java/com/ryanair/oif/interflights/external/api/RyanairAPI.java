package com.ryanair.oif.interflights.external.api;

import com.ryanair.oif.interflights.external.api.domain.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RyanairAPI {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${ryanair.api.endpointURL}")
    String endpoint;

    public List<Route> getRoutes(){
        String finalEndPoint = endpoint + "/core/3/routes/";
        ResponseEntity<Route[]> responseEntity = restTemplate.getForEntity(finalEndPoint, Route[].class);
        Object[] objects = responseEntity.getBody();
        return null;
    }
}
