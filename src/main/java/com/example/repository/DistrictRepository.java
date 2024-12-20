package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.District;

public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByStateId(Long stateId);

}
