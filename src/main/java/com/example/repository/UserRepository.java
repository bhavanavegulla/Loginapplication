package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

	List<User> findByCreatedBy(String admin);
	   User findByFullname(String fullname);
}
