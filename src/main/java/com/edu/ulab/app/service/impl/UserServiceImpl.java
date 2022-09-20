package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.UserStore;
import com.edu.ulab.app.utils.GeneratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final String USER_NOT_FOUND = "User with id=%s not found";
    private final UserMapper userMapper;
    private final UserStore userStore;

    public UserServiceImpl(UserMapper mapper, UserStore userStore) {
        this.userMapper = mapper;
        this.userStore = userStore;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Got user create request: {}", userDto);
        // сгенерировать идентификатор
        Long id = GeneratorUtils.nextID();
        log.info("Generated id: {}", id);
        userDto.setId(id);
        User toCreate = userMapper.userDtoToUserEntity(userDto);
        // создать пользователя
        User created = userStore.create(toCreate);
        // вернуть сохраненного пользователя со всеми необходимыми полями id
        return userMapper.userEntityToUserDto(created);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Long id = userDto.getId();
        User toUpdate = userMapper.userDtoToUserEntity(userDto);
        if (userStore.getById(toUpdate.getId()).isEmpty()){
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
        }
        User updated = userStore.update(toUpdate);
        return userMapper.userEntityToUserDto(updated);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Got user request by id: {}", id);
        return userStore.getById(id)
                .map(userMapper::userEntityToUserDto)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Got user delete request by id: {}", id);
        userStore.getById(id)
                .ifPresentOrElse(userStore::delete,
                        () -> {
                           throw new NotFoundException(String.format(USER_NOT_FOUND, id));
                        });
    }

    @Override
    public List<Book> getAllBooks(Long userId) {
        log.info("Got all user books request by id: {}", userId);
        if (userStore.getById(userId).isEmpty()){
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }
        return userStore.getAllBooks(userId);
    }
}
