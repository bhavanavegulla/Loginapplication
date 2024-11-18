package com.example.service;

import java.util.List;
import com.example.dto.UserDto;
import com.example.model.User;

public interface UserService {
    User findByUsername(String username);
    User save(UserDto userDto);
    List<User> findAllUsers();
    User findById(Long id);
	List<User> findUsersCreatedBy(String admin);
	void deleteUserById(Long id);
    User save(User user); 
 
	User getByFullname(String fullname);
	
	
	
}
