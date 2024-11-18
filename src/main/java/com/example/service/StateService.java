package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.State;
import com.example.repository.StateRepository;

@Service
public class StateService {
	 @Autowired
	    private StateRepository stateRepository;
	 
	 public StateService(StateRepository stateRepository) {
	        this.stateRepository = stateRepository;
	    }
	    // Method to retrieve all states
	    public List<State> getAllStates() {
	        return stateRepository.findAll();
	    }

	    // Method to save a state
	    public State saveState(State state) {
	        return stateRepository.save(state);
	    }
}
