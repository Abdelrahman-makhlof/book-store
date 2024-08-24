package com.example.userservice.service;

import com.learning.bookstore.common.dto.UserDTO;
import com.learning.bookstore.common.entity.User;
import com.learning.bookstore.common.repository.UserRepository;
import com.learning.bookstore.common.util.Util;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserDTO addUser(UserDTO userDTO) {
        userRepository.save(Util.map(modelMapper, userDTO, User.class));
        return userDTO;
    }


    public UserDTO deleteUser(UserDTO userDTO) {
        userRepository.delete(Util.map(modelMapper, userDTO, User.class));
        return userDTO;
    }

    public UserDTO updateUser(UserDTO userDTO) {
        var user = userRepository.save(Util.map(modelMapper, userDTO, User.class));
        return Util.map(modelMapper, user, UserDTO.class);
    }

    public UserDTO getUserByUsername(String username) {
        var user = userRepository.getUserByUsername(username);
        return Util.map(modelMapper, user, UserDTO.class);
    }

    public UserDTO getUserByEmail(String email) {
        var user = userRepository.getUserByEmail(email);
        return Util.map(modelMapper, user, UserDTO.class);
    }

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> Util.map(modelMapper, user, UserDTO.class)).toList();
    }

}
