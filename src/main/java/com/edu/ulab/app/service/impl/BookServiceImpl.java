package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final String BOOK_NOT_FOUND = "Book with id=%s not found";

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        log.info("Request to update book: {}", bookDto);
        Long id = bookDto.getId();
        Book toUpdate = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", toUpdate);
        log.info("Finded book: {}", bookRepository.findByIdForUpdate(id));
        return bookRepository.findByIdForUpdate(id)
                .map(src -> {
                    BeanUtils.copyProperties(toUpdate, src);
                    return src;
                })
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new NotFoundException(String.format(BOOK_NOT_FOUND, id)));
    }

    @Override
    public BookDto getBookById(Long id) {
        log.info("Request to got book by id: {}", id);
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new NotFoundException(String.format(BOOK_NOT_FOUND, id)));
    }

    @Override
    public void deleteBookById(Long id) {
        log.info("Request to delete book by id: {}", id);
        bookRepository.findById(id)
                .ifPresentOrElse(bookRepository::delete,
                        () -> {
                            throw new NotFoundException(String.format(BOOK_NOT_FOUND, id));
                        });
    }
}
