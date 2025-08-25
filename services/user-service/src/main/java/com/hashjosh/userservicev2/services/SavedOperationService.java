package com.hashjosh.userservicev2.services;

import com.hashjosh.userservicev2.dto.UserRequestDto;
import com.hashjosh.userservicev2.mapper.UserMapper;
import com.hashjosh.userservicev2.models.Role;
import com.hashjosh.userservicev2.models.User;
import com.hashjosh.userservicev2.models.UserProfile;
import com.hashjosh.userservicev2.repository.UserProfileRepository;
import com.hashjosh.userservicev2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SavedOperationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserProfileRepository userProfileRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public User saveUser(UserRequestDto dto, Role role) {
        User user = userRepository.save(userMapper.toRegisterStaff(dto, role));
        UserProfile userProfile = userProfileRepository.save(userMapper.toUserProfile(dto.userProfile(), user));

        user.setUserProfile(userProfile);
        user.setRole(role);

        return user;
    }
}
