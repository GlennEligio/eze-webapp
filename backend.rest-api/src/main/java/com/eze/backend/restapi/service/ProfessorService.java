package com.eze.backend.restapi.service;

import com.eze.backend.restapi.repository.exception.ApiException;
import com.eze.backend.restapi.model.Professor;
import com.eze.backend.restapi.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService implements IService<Professor>{

    @Autowired
    private ProfessorRepository repository;

    @Override
    public List<Professor> getAll() {
        return repository.findAll();
    }

    @Override
    public Professor get(Serializable name) {
        return repository.findByName(name.toString()).orElseThrow(() -> new ApiException(notFound(name), HttpStatus.NOT_FOUND));
    }

    @Override
    public Professor create(Professor professor) {
        if(professor.getName() != null) {
            Optional<Professor> opProf = repository.findByName(professor.getName());
            if(opProf.isPresent()) {
                throw new ApiException(alreadyExist(professor.getName()), HttpStatus.BAD_REQUEST);
            }
        }
        return repository.save(professor);
    }

    @Override
    public Professor update(Professor professor, Serializable name) {
        Professor professor1 = repository.findByName(name.toString()).orElseThrow(() -> new ApiException(notFound(name), HttpStatus.NOT_FOUND));
        professor1.update(professor);
        return repository.save(professor1);
    }

    @Override
    public void delete(Serializable name) {
        Professor professor1 = repository.findByName(name.toString()).orElseThrow(() -> new ApiException(notFound(name), HttpStatus.NOT_FOUND));
        repository.delete(professor1);
    }

    @Override
    public String notFound(Serializable name) {
        return "No professor with name " + name + " was found";
    }

    @Override
    public String alreadyExist(Serializable name) {
        return "Professor with name " + name + " already exist";
    }
}
