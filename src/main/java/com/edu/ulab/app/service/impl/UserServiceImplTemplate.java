package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    public final UserMapper userMapper;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate,
                                   UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);
        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved user: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        final String INSERT_SQL = "UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
        int updateStatus = jdbcTemplate.update(INSERT_SQL,
                        userDto.getFullName(),
                        userDto.getTitle(),
                        userDto.getAge(),
                        userDto.getId()
        );
        if (updateStatus == 1) {
            log.info("Updated user: = {}", userDto.getId());
        }
        return getUserById(userDto.getId());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Request to got user by id: {}", id);
        final String INSERT_SQL = "SELECT * FROM PERSON WHERE ID = ?";
        Person person = null;
        try {
            person = jdbcTemplate.queryForObject(INSERT_SQL, (rs, rowNum) ->
                            new Person(
                                    rs.getLong("id"),
                                    rs.getString("FULL_NAME"),
                                    rs.getString("TITLE"),
                                    rs.getInt("AGE")
                            ),
                    id);
        } catch (DataAccessException e) {
            throw new NotFoundException("User by id not found");
        }
        return userMapper.personToUserDto(person);
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Request to delete user by id: {}", id);
        final String INSERT_SQL = "DELETE FROM PERSON WHERE ID = ?";
        Object[] args = new Object[]{id};
        jdbcTemplate.update(INSERT_SQL, args);
    }

    @Override
    public List<Book> getAllBooksById(Long id) {
        log.info("Request to get user books by user id: {}", id);
        final String INSERT_SQL = "SELECT * FROM BOOK WHERE USER_ID = ?";
        return jdbcTemplate.query(
                INSERT_SQL,
                (rs, rowNum) -> new Book(
                        rs.getLong("ID"),
                        rs.getLong("USER_ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getLong("PAGE_COUNT")
                ),
                id
        );
    }
}
