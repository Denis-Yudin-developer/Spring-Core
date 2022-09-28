package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    UserDto getUserById(Long id);

    void deleteUserById(Long id);

    List<Book> getAllBooksById(Long id);
}
