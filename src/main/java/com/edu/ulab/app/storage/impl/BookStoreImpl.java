package com.edu.ulab.app.storage.impl;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.storage.BookStore;
import com.edu.ulab.app.storage.UserStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;

import java.util.HashMap;
import java.util.Optional;

@Repository
@Slf4j
public class BookStoreImpl implements BookStore {
    private final String USER_NOT_FOUND = "User with id=%s not found";
    private final HashMap<Long, Book> books = new HashMap<>();
    private final UserStore userStore;

    public BookStoreImpl(UserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    public Book create(Book book) {
        Book createdBook = books.put(book.getId(), book);
        Long userId = createdBook.getUserId();
        if (userId != null){
            addUserToBook(userId, book);
        }
        return books.get(book.getId());
    }

    @Override
    public Book update(Book book) {
        Book updatedBook = books.put(book.getId(), book);
        log.info("Updated user: {}", updatedBook);
        Long userId = updatedBook.getUserId();
        if (userId != null){
            addUserToBook(userId, book);
        }
        return updatedBook;
    }

    @Override
    public Optional<Book> getById(Long id) {
        log.info("Got user by id: {}", id);
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public void delete(Book book) {
        Book deletedBook = books.remove(book.getId());
        log.info("Deleted user: {}", deletedBook);
    }

    private void addUserToBook(Long userId, Book book){
        userStore.getById(userId)
                .ifPresentOrElse(user -> {
                    var userBooks = user.getBooks();
                    userBooks.add(book);
                    user.setBooks(userBooks);
                }, () -> {
                    throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
                });
    }
}
