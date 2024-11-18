package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.District;
import com.example.repository.DistrictRepository;

@Service
public class DistrictService {
	@Autowired
    private DistrictRepository districtRepository;
	 public DistrictService(DistrictRepository districtRepository) {
	        this.districtRepository = districtRepository;
	    }
    // Method to retrieve districts based on stateId
	 public List<District> getDistrictsByStateId(Long stateId) {
		    return districtRepository.findByStateId(stateId);
		}


    
}
