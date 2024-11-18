package com.example.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name="users")
public class User {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String username;
   @Pattern(
	        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.*[0-9]).{6,}$", 
	        message = "Password must be at least 8 characters long, contain one capital letter, one special character, and one number"
	    )
   private String password;
   private String fullname;
   private String role;
   private String createdBy;
 
  
public User() {
	
}

public User( String username, String password, String fullname, String role,String createdBy) {
	

	this.username = username;
	this.password = password;
	this.fullname = fullname;
	 this.role = role;
	this.createdBy= createdBy;
	
}

public String getCreatedBy() {
	return createdBy;
}
public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String getFullname() {
	return fullname;
}
public void setFullname(String fullname) {
	this.fullname = fullname;
}


	

}
