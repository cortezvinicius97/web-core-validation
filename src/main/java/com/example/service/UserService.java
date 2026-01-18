package com.example.service;

import com.example.dto.UserDto;
import com.example.exception.UserNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.vcinsidedigital.webcore.annotations.Inject;
import com.vcinsidedigital.webcore.annotations.Service;

import java.util.List;

@Service
public class UserService
{
    @Inject
    private UserRepository repository;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(UserDto userDto) {
        User user = new User();
        user.setId(null); // Garante que será um novo ID
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return repository.save(user);
    }
}
