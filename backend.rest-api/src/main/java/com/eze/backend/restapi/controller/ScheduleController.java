package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.RoomSchedule;
import com.eze.backend.restapi.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ScheduleController {

    @Autowired
    private ScheduleService service;

    @GetMapping("/schedules")
    public ResponseEntity<List<RoomSchedule>> getSchedules() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/schedules/{code}")
    public ResponseEntity<RoomSchedule> getSchedule(@PathVariable("code") String code) {
        return ResponseEntity.ok(service.get(code));
    }

    @PostMapping("/schedules")
    public ResponseEntity<RoomSchedule> createSchedule(@RequestBody RoomSchedule roomSchedule) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(roomSchedule));
    }

    @PutMapping("/schedules/{code}")
    public ResponseEntity<RoomSchedule> updateSchedule(@RequestBody RoomSchedule roomSchedule,
                                                       @PathVariable("code") String code) {
        return ResponseEntity.ok(service.update(roomSchedule, code));
    }

    @DeleteMapping("/schedules/{code}")
    public ResponseEntity<Object> deleteSchedule(@PathVariable("code") String code) {
        service.delete(code);
        return ResponseEntity.ok().build();
    }
}
