package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.BookStore;
import com.edu.ulab.app.utils.GeneratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final String BOOK_NOT_FOUND = "Book with id=%s not found";
    private final BookMapper bookMapper;
    private final BookStore bookStore;

    public BookServiceImpl(BookMapper mapper, BookStore bookStore) {
        this.bookMapper = mapper;
        this.bookStore = bookStore;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        log.info("Got book create request: {}", bookDto);
        Long id = GeneratorUtils.nextID();
        bookDto.setId(id);
        Book toCreate = bookMapper.bookDtoToBookEntity(bookDto);
        Book created = bookStore.create(toCreate);
        return bookMapper.bookEntityToBookDto(created);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        log.info("Update book create request: {}", bookDto);
        Long id = bookDto.getId();
        Book toUpdate = bookMapper.bookDtoToBookEntity(bookDto);
        if (bookStore.getById(toUpdate.getId()).isEmpty()){
            throw new NotFoundException(String.format(BOOK_NOT_FOUND, id));
        }
        Book updated = bookStore.update(toUpdate);
        return bookMapper.bookEntityToBookDto(updated);
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Got book request by id: {}", id);
        return bookStore.getById(id)
                .map(bookMapper::bookEntityToBookDto)
                .orElseThrow(() -> new NotFoundException(String.format(BOOK_NOT_FOUND, id)));
    }

    @Override
    public void deleteBookById(Long id) {
        log.info("Got user delete request by id: {}", id);
        bookStore.getById(id)
                .ifPresentOrElse(bookStore::delete,
                        () -> {
                            throw new NotFoundException(String.format(BOOK_NOT_FOUND, id));
                        });
    }
}
