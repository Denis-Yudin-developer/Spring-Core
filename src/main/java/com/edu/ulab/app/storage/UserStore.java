package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;

import java.util.List;

public interface UserStore extends Store<User>{
    List<Book> getAllBooks(Long userId);
}
