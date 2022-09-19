package com.edu.ulab.app.storage.impl;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.storage.UserStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserStoreImpl implements UserStore {
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        User createdUser = users.put(user.getId(), user);
        log.info("Created user: {}", createdUser);
        return users.get(user.getId());
    }

    @Override
    public User update(User entity) {
        User updatedUser = users.put(entity.getId(), entity);
        log.info("Updated user: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public Optional<User> getById(Long id) {
        log.info("Got user by id: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(User user) {
        User deletedUser = users.remove(user.getId());
        log.info("Deleted user: {}", deletedUser);
    }

    @Override
    public List<Book> getAllBooks(Long userId) {
        return users.get(userId).getBooks();
    }
}
