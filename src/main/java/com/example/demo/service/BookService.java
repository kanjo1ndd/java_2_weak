package com.example.demo.service;

import com.example.demo.dto.BookCreateDto;
import com.example.demo.dto.BookDto;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;

    public BookService(BookRepository bookRepo, AuthorRepository authorRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
    }

    @SuppressWarnings("null")
    public BookDto create(BookCreateDto dto) {
        Author author = authorRepo.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Book b = new Book();
        b.setTitle(dto.getTitle());
        b.setYear_published(dto.getYear_published());
        b.setGenre(dto.getGenre());
        b.setAuthor(author);

        return toDto(bookRepo.save(b));
    }

    @SuppressWarnings("null")
    public BookDto update(Long id, BookCreateDto dto) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTitle(dto.getTitle());
        book.setYear_published(dto.getYear_published());
        book.setGenre(dto.getGenre());

        if (dto.getAuthorId() != null) {
            Author author = authorRepo.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));
            book.setAuthor(author);
        }

        return toDto(bookRepo.save(book));
    }

    @SuppressWarnings("null")
    public void delete(Long id) {
        if (!bookRepo.existsById(id)) throw new RuntimeException("Book not found");
        bookRepo.deleteById(id);
    }

    @SuppressWarnings("null")
    public Book getById(Long id) {
        return bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public BookDto toDto(Book b) {
        BookDto d = new BookDto();
        d.setId(b.getId());
        d.setTitle(b.getTitle());
        d.setYear_published(b.getYear_published());
        d.setGenre(b.getGenre());

        Author a = b.getAuthor();
        if (a != null) {
            com.example.demo.dto.AuthorDto ad = new com.example.demo.dto.AuthorDto();
            ad.setId(a.getId());
            ad.setName(a.getName());
            d.setAuthor(ad);
        }

        return d;
    }

    public Page<Book> list(Long authorId, String title, int page, int size) {
        return bookRepo.listFiltered(authorId, title, PageRequest.of(Math.max(0, page - 1), size));
    }

    public Author createOrFindAuthorByName(String name) {
        if (name == null || name.isBlank()) return null;
        return authorRepo.findByName(name).orElseGet(() -> authorRepo.save(new Author(name)));
    }
}