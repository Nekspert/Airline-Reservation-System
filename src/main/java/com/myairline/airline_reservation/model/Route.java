package com.myairline.airline_reservation.model;

import jakarta.persistence.*;
import java.time.Duration;

@Entity
@Table(name = "routes")
public class Route {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;
    private String destination;
    private long durationMinutes;

    public Route() {}
    public Route(String origin, String destination, Duration duration) {
        this.origin = origin;
        this.destination = destination;
        this.durationMinutes = duration.toMinutes();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toString() {
        return origin + " → " + destination + " (" + durationMinutes + " мин)";
    }
}
