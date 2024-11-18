package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.State;

public interface StateRepository extends JpaRepository<State, Long> {
	List<State> findAll();
}
