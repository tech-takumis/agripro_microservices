package com.hashjosh.userservicev2.controller;

import com.hashjosh.userservicev2.config.CustomUserDetails;
import com.hashjosh.userservicev2.dto.AuthenticatedUserDto;
import com.hashjosh.userservicev2.dto.UserResponseDto;
import com.hashjosh.userservicev2.services.AuthService;
import com.hashjosh.userservicev2.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll(),HttpStatus.FOUND);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<UserResponseDto>> getAllDeletedUsers() {
        return new ResponseEntity<>(userService.findAllDeletedUser(),HttpStatus.FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable int id) {
        return new ResponseEntity<>(userService.findUser(id), HttpStatus.FOUND);
    }

    @GetMapping("/token")
    public ResponseEntity<?> getToken(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !(auth.getPrincipal() instanceof  CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));
        }

        String jwt = authService.generateToken(userDetails.user());

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDto> user(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAuthenticateUser());
    }



//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> deleteUser(
            @PathVariable int id
    ){
        try{
            userService.softDelete(id);
            return ResponseEntity.status(HttpStatus.GONE).body(
                    Map.of("message", "User "+id+" deleted successfully")
            );
        }catch(EmptyResultDataAccessException ex){
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<String> restoreUser(
            @PathVariable long id
    ){
        return new ResponseEntity<>(userService.restore(id),HttpStatus.PARTIAL_CONTENT);
    }


}
