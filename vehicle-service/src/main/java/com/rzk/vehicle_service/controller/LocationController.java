package com.rzk.vehicle_service.controller;

import com.rzk.vehicle_service.model.Location;
import com.rzk.vehicle_service.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationRepository locationRepository;

    @PostMapping
    public Location create(@RequestBody Location location) {
        return locationRepository.save(location);
    }

    @GetMapping
    public List<Location> getAll() {
        return locationRepository.findAll();
    }
}
