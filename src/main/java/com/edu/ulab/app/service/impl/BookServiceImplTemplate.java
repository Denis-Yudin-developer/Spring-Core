package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {
    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;



    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate,
                                   BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);
        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved book: {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        final String INSERT_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, USER_ID = ? WHERE ID = ?";
        int updateStatus = jdbcTemplate.update(INSERT_SQL,
                        bookDto.getTitle(),
                        bookDto.getAuthor(),
                        bookDto.getPageCount(),
                        bookDto.getUserId(),
                        bookDto.getId()
        );
        if (updateStatus == 1) {
            log.info("Updated book: = {}", bookDto.getId());
        }
        return getBookById(bookDto.getId());
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Request to got book by id: {}", id);
        final String INSERT_SQL = "SELECT * FROM BOOK WHERE ID = ?";
        Book book = jdbcTemplate.queryForObject(INSERT_SQL, (rs, rowNumm) ->
                new Book(
                        rs.getLong("id"),
                        rs.getLong("userId"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getLong("pageCount")
                ),
                "id");
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        log.info("Request to delete book by id: {}", id);
        final String INSERT_SQL = "DELETE FROM BOOK WHERE ID = ?";
        Object[] args = new Object[]{id};
        jdbcTemplate.update(INSERT_SQL, args);
    }
}
