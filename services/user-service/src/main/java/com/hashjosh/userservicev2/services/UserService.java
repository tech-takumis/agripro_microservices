package com.hashjosh.userservicev2.services;

import com.hashjosh.userservicev2.dto.AuthenticatedUserDto;
import com.hashjosh.userservicev2.dto.UserRequestDto;
import com.hashjosh.userservicev2.dto.UserResponseDto;
import com.hashjosh.userservicev2.exceptions.RoleNotFoundException;
import com.hashjosh.userservicev2.exceptions.UserAlreadyExistException;
import com.hashjosh.userservicev2.exceptions.UserNotAuthenticatedException;
import com.hashjosh.userservicev2.mapper.UserMapper;
import com.hashjosh.userservicev2.models.Role;
import com.hashjosh.userservicev2.models.User;
import com.hashjosh.userservicev2.repository.RoleRepository;
import com.hashjosh.userservicev2.repository.UserProfileRepository;
import com.hashjosh.userservicev2.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final SavedOperationService savedOperationService;


    public UserResponseDto findUser(int id) {
        User  user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return mapper.toStaffResponse(user);

    }

    public List<UserResponseDto> findAll() {
        return userRepository.findAllNotDeletedUser().stream()
                .map(mapper::toStaffResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> findAllDeletedUser() {
        return userRepository.findAllDeletedUser().stream().map(
                mapper::toStaffResponse
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthenticatedUserDto getAuthenticateUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null){
            throw new UserNotAuthenticatedException(
                    "User is not authenticated",
                    "User is not authenticated",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }

        String username = authentication.getName();

        User user =  userRepository.findUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return mapper.toAuthenticatedUser(user);

    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public User register(UserRequestDto dto) throws MessagingException {

        if(userRepository.findEmail(dto.email()).isPresent()){
            throw new UserAlreadyExistException(
                    "User with email "+ dto.email() + "already exist please try again",
                    HttpStatus.BAD_REQUEST.value(),
                    "User already exist"
            );
        }

        Role role = roleRepository.findById(dto.roleId()).orElseThrow(
                () -> new RoleNotFoundException(
                        "Role id not found",
                        "Role not found",
                        HttpStatus.NOT_FOUND.value()
                )
        );

        return savedOperationService.saveUser(dto,role);
    }


    public void softDelete(int id) {
        userRepository.softDelete(id);
    }

    public String restore(long id) {
        userRepository.restore(id);
        return "User restored successfully";
    }

    public Role getUserRole(Long id) {

        Optional<Role> role = roleRepository.findById(id);

        if(role.isEmpty()){
            throw new RoleNotFoundException(
                    "Role id "+id+"not found!",
                    "Role not found",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        return role.get();
    }
}
