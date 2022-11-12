package com.eze.backend.spring.service;

import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.RoomSchedule;
import com.eze.backend.spring.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService implements IService<RoomSchedule> {

    private ScheduleRepository repository;

    @Override
    public List<RoomSchedule> getAll() {
        return repository.findAll();
    }

    @Override
    public List<RoomSchedule> getAllNotDeleted() {
        return null;
    }

    @Override
    public RoomSchedule get(Serializable code) {
        return repository.findByScheduleCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
    }

    @Override
    public RoomSchedule create(RoomSchedule roomSchedule) {
        String tobeScheduleCode = createScheduleCode(roomSchedule);
        Optional<RoomSchedule> schedOp = repository.findByScheduleCode(tobeScheduleCode);
        if(schedOp.isPresent()) {
            throw new ApiException(alreadyExist(tobeScheduleCode), HttpStatus.BAD_REQUEST);
        }
        roomSchedule.setScheduleCode(tobeScheduleCode);
        return repository.save(roomSchedule);
    }

    @Override
    public RoomSchedule update(RoomSchedule roomSchedule, Serializable code) {
        RoomSchedule roomSchedule1 = repository.findByScheduleCode(code.toString())
                .orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        // Save previous code
        String previousScheduleCode = roomSchedule1.getScheduleCode();
        roomSchedule1.update(roomSchedule);
        String tobeScheduleCode = createScheduleCode(roomSchedule1);

        // check if the new Code exist already and if newCode != previousCode
        Optional<RoomSchedule> schedule = repository.findByScheduleCode(tobeScheduleCode);
        if(schedule.isPresent() && !previousScheduleCode.equals(tobeScheduleCode)) {
            throw new ApiException(alreadyExist(tobeScheduleCode), HttpStatus.BAD_REQUEST);
        }
        return repository.save(roomSchedule1);
    }

    @Override
    public void delete(Serializable code) {
        RoomSchedule roomSchedule1 = repository.findByScheduleCode(code.toString())
                .orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        repository.delete(roomSchedule1);
    }

    @Override
    public void softDelete(Serializable id) {

    }

    @Override
    public String notFound(Serializable code) {
        return "No schedule with code " + code + " was found";
    }

    @Override
    public String alreadyExist(Serializable code) {
        return "Schedule with same code " + code + " already exist";
    }

    @Override
    public int addOrUpdate(List<RoomSchedule> entities, boolean overwrite) {
        return 0;
    }

    // TODO: Create Custom Generator for the Schedule entity schedule code with Day, Time, and Room
    public String createScheduleCode(RoomSchedule roomSchedule) {
        return "RM-" + roomSchedule.getDay() + "-" + roomSchedule.getTime() + "-" + roomSchedule.getRoom();
    }
}
