package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImplTemplate userService;
    private final BookServiceImplTemplate bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImplTemplate userService,
                          BookServiceImplTemplate bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest, Long userId) {
        log.info("Request to update by: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        userDto.setId(userId);
        log.info("Mapped user request: {}", userDto);

        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", updatedUser);

        userService.getAllBooksById(updatedUser.getId())
                .forEach(book -> bookService.deleteBookById(book.getId()));

        log.info("Updated userId: {} book list", updatedUser.getId());
        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(updatedUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Updated book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(updatedUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Request to got user by id: {}", userId);

        UserDto userById = userService.getUserById(userId);
        log.info("Got user request: {}", userById);
        List<Long> bookIdList = userService.getAllBooksById(userId).stream()
                .filter(Objects::nonNull)
                .peek(filteredBookDto -> log.info("Get book: {} of the user id: {}", filteredBookDto, userId))
                .map(Book::getId)
                .toList();

        return UserBookResponse.builder()
                .userId(userById.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        log.info("Request to delete user with books, user id: {}", userId);

        userService.getAllBooksById(userId).stream()
                .filter(Objects::nonNull)
                .peek(deleteBook -> log.info("Deleted book: {} of the user id: {}", deleteBook, userId))
                .forEach(deleteBook -> bookService.deleteBookById(deleteBook.getId()));

        userService.deleteUserById(userId);
    }
}
