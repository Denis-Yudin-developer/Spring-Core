package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final String USER_NOT_FOUND = "User with id=%s not found";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        log.info("Request to update user: {}", userDto);
        Long id = userDto.getId();
        Person toUpdate = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", toUpdate);
        log.info("Finded book: {}", userRepository.findByIdForUpdate(id));
        return userRepository.findByIdForUpdate(id)
                .map(src -> {
                    BeanUtils.copyProperties(toUpdate, src);
                    return src;
                })
                .map(userMapper::personToUserDto)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Request to got user by id: {}", id);
        return userRepository.findById(id)
                .map(userMapper::personToUserDto)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Request to delete user by id: {}", id);
        userRepository.findById(id)
                .ifPresentOrElse(userRepository::delete,
                        () -> {
                            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
                        });
    }

    @Override
    public List<Book> getAllBooksById(Long id) {
        log.info("Request to get user books by id: {}", id);
        List<Book> userBooks = userRepository.findAllBooksById(id);
        if(userBooks == null){
            userBooks = new ArrayList<>();
        }
        return userBooks;
    }
}
