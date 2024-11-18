package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dto.UserDto;
import com.example.model.District;
import com.example.model.State;

import com.example.model.User;

import com.example.service.DistrictService;
import com.example.service.StateService;
import com.example.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StateService stateService;

    @Autowired
    private DistrictService districtService;
  
   
    
    public UserController(StateService stateService, DistrictService districtService) {
        this.stateService = stateService;
        this.districtService = districtService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Set cache control headers for sensitive pages
    private void setCacheControlHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    // Default routing after login based on roles
    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        if (authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";  // Redirect to admin page for admin users
        } else if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MANAGER"))) {
            return "redirect:/admin"; 
        }

        return "redirect:/home";    // Redirect to home page for regular users
    }

    // Home page for regular users
    @GetMapping("/home")
    public String home(Model model, Authentication authentication, HttpServletResponse response) {
        setCacheControlHeaders(response);  // Set cache control headers

        if (authentication == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("role", currentUser.getRole());
        return "home";  // Return home page for regular users
    }

    // Login page
    @GetMapping("/login")
    public String login(Model model, Principal principal,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {
        if (principal != null) {
            return "redirect:/default"; // Redirect to default after login if already logged in
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password."); // Set error message
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully."); // Set success message
        }
        model.addAttribute("user", new UserDto());
        return "login"; // Return login page
    }
    

 // Registration form
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserDto());
        List<State> states = stateService.getAllStates();
        System.out.println(states); // Log states to verify they are fetched correctly
        model.addAttribute("states", states);
        return "register"; // Return register form
    }


    // Handle registration of new users
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("states", stateService.getAllStates()); // Fetch states if validation fails
            return "register";  // Return back to register page if validation fails
        }

        if (userService.findByUsername(userDto.getUsername()) != null) {
            model.addAttribute("userexist", true);
            model.addAttribute("states", stateService.getAllStates()); // Fetch states if user exists
            return "register";  // Return back if user already exists
        }

        userService.save(userDto);
        model.addAttribute("successMessage", "Registration Successful! Please login."); // Success message
        return "redirect:/login?success";  // Redirect to login page after successful registration
    }
    @GetMapping("/districts")
    public @ResponseBody List<District> getDistrictsByState(@RequestParam("stateId") Long stateId) {
        System.out.println("Requested State ID: " + stateId); // Log the requested state ID
        List<District> districts = districtService.getDistrictsByStateId(stateId);
        
        if (districts.isEmpty()) {
            System.out.println("No districts found for State ID: " + stateId);
        } else {
            System.out.println("Districts found: " + districts); // Log the districts found
        }
        
        return districts; // Ensure this returns the correct list
    }



 // Admin dashboard
    @GetMapping("/admin")
    public String adminDashboard(Model model, Authentication authentication, HttpServletResponse response) {
        setCacheControlHeaders(response);  // Set cache control headers

        if (authentication == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        // Fetch all users regardless of who created them
        List<User> users = userService.findAllUsers(); // This method needs to be implemented in your UserService

        model.addAttribute("username", authentication.getName());  // Admin's username
        model.addAttribute("users", users);  // Add the complete list of users to the model
        model.addAttribute("user", new UserDto()); // Add an empty UserDto object to the model for the form binding
        return "admin";  // Return the admin dashboard
    }

    // Display form to create a new user (only accessible by Admin)
    @GetMapping("/user/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new UserDto()); // Create a new UserDto object for the form
        return "createUser";  // Return create user form template
    }

    @PostMapping("/user/create")
    public String createUser(@Valid @ModelAttribute("user") UserDto userDto, 
                             BindingResult result, 
                             RedirectAttributes redirectAttributes, 
                             Principal principal) {
        if (result.hasErrors()) {
            return "createUser"; // Return back to create user page if validation fails
        }

        if (userService.findByUsername(userDto.getUsername()) != null) {
            redirectAttributes.addFlashAttribute("error", "User already exists!");
            return "redirect:/user/create"; // Redirect back if user already exists
        }

        // Get the role of the current logged-in user
        String currentUserRole = userService.findByUsername(principal.getName()).getRole();

        // Check the role of the current user and restrict creation accordingly
        if ("Manager".equalsIgnoreCase(currentUserRole)) {
            // Managers can only create users and cannot create Admin or Manager roles
            if ("Admin".equalsIgnoreCase(userDto.getRole()) || 
                "Manager".equalsIgnoreCase(userDto.getRole())) {
                redirectAttributes.addFlashAttribute("error", "You cannot create a user with Admin or Manager role!");
                return "redirect:/user/create"; // Redirect back with error
            }
        } else if ("Admin".equalsIgnoreCase(currentUserRole)) {
            // Admin can create only users or managers, but not admins
            if ("Admin".equalsIgnoreCase(userDto.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Admin role cannot be created!");
                return "redirect:/user/create"; // Redirect back with error
            }
        }
     
        // Set the creator of the user as the current logged-in admin
        userDto.setCreatedBy(principal.getName());
        userService.save(userDto);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully!"); // Success message
        return "redirect:/admin";  // Redirect back to admin dashboard
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User user = userService.findById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin";  // Redirect to admin if the user doesn't exist
        }

        userService.deleteUserById(id);  // Delete the user by ID
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!"); // Add success message
        return "redirect:/admin";  // Redirect back to the admin dashboard after deletion
    }

    // View user profile
    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        String username = principal.getName(); // Get the username of the logged-in user
        User user = userService.findByUsername(username); // Fetch the user details from the service
        model.addAttribute("user", user); // Add user details to the model
        return "profile"; // Return the view name for the profile page
    }

    // View the list of users created by a specific admin
    @GetMapping("/users/createdBy")
    public String usersCreatedBy(@RequestParam("admin") String admin, Model model) {
        List<User> usersCreatedByAdmin = userService.findUsersCreatedBy(admin);
        model.addAttribute("admin", admin);  // Pass the admin name
        model.addAttribute("users", usersCreatedByAdmin);  // Add users list to the model
        return "usersCreatedBy";  // Return the view to show users created by a specific admin
    } 

   
 // View user details
    @GetMapping("/user/details/{id}")
    public String userDetails(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id);  // Fetch the user by ID
        if (user == null) {
            // Handle user not found
            model.addAttribute("error", "User not found");
            return "errorPage";  // Redirect to an error page or return a relevant message
        }

        model.addAttribute("userdetail", user); // Add user details to the model
        return "userDetails"; // Return view for user details
    }

    // Update user profile
    @GetMapping("/user/update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id); // Fetch user by ID
        if (user == null) {
            // Handle user not found
            model.addAttribute("error", "User not found");
            return "errorPage";  // Redirect to an error page or return a relevant message
        }

        model.addAttribute("user", user); // Add user details to the model
        return "updateUser"; // Return the update user form
    }

    // Handle updating the user
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("user") UserDto userDto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             Principal principal) { // Add Principal here
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "updateUser"; // Show the update form again if there are errors
        }
 
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin"; // Redirect to admin if the user doesn't exist
        }

        // Get the username of the person making the update
        String updatedBy = principal.getName(); // This retrieves the logged-in user's username

        // Append the new fullname with the updatedBy username
        String newFullname = (existingUser.getFullname() != null ? existingUser.getFullname() : "") + 
                             ", " + userDto.getFullname() + "-" + updatedBy;

        existingUser.setFullname(newFullname.trim()); // Set the updated fullname
        existingUser.setRole(userDto.getRole()); // Update the user's role

        // Save the updated  user
        userService.save(existingUser);
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        return "redirect:/admin"; // Redirect back to admin dashboard
    }
    @GetMapping("/{fullname}")
    public ResponseEntity<?> getByFullname(@PathVariable ("fullname")String fullname) {
        User user = userService.getByFullname(fullname);
        if (user != null) {
            return ResponseEntity.ok(user); // Return the user if found
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }
    }
  
}

