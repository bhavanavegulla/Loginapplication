package com.example.dto;


import jakarta.validation.constraints.Pattern;

public class UserDto {
	
	 private Long id;
	 public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private String username;

	 @Pattern(
		        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.*[0-9]).{6,}$", 
		        message = "Password must be at least 8 characters long, contain one capital letter, one special character, and one number"
		    )
	   private String password;
	   private String fullname;
	   private String role ;
	   private String createdBy;
	   private Long stateId;      
	    private Long districtId;
	  
	    

	public UserDto() {
			
		}
		
	public UserDto(String username, String password, String fullname,String role, String createdBy,Long stateId,Long districtId) {
		
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.role=role;
		this.createdBy= createdBy;
		this.stateId = stateId;
		 this.districtId = districtId;
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
	 public Long getStateId() {
	        return stateId;
	    }

	    public void setStateId(Long stateId) {
	        this.stateId = stateId;
	    }

	    public Long getDistrictId() {
	        return districtId;
	    }

	    public void setDistrictId(Long districtId) {
	        this.districtId = districtId;
	    }
}
