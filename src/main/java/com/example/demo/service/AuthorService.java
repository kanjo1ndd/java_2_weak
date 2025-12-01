package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository repo;
    public AuthorService(AuthorRepository repo) { this.repo = repo; }

    public List<Author> list() { return repo.findAll(); }

    public Author create(Author a) {
        if (repo.existsByName(a.getName()))
            throw new IllegalArgumentException("Author with this name already exists");
        return repo.save(a);
    }

    @SuppressWarnings("null")
    public Author update(Long id, Author data) {
        Author ex = repo.findById(id).orElseThrow(() -> new RuntimeException("Author not found"));
        if (!ex.getName().equals(data.getName()) && repo.existsByName(data.getName()))
            throw new IllegalArgumentException("Author with this name already exists");
        ex.setName(data.getName());
        return repo.save(ex);
    }

    @SuppressWarnings("null")
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new RuntimeException("Author not found");
        repo.deleteById(id);
    }
}
