package com.ryanair.oif.routes.external.api.domain;

import java.util.List;

public class MonthFlights {

    private String month;
    private List<DayFlights> days;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<DayFlights> getDays() {
        return days;
    }

    public void setDays(List<DayFlights> days) {
        this.days = days;
    }
}
