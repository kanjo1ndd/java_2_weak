package com.example.demo.controller;

import com.example.demo.entity.Author;
import com.example.demo.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/author")
public class AuthorController {
    private final AuthorService service;
    public AuthorController(AuthorService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Author>> list() { return ResponseEntity.ok(service.list()); }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<Author> create(@RequestBody Author a) {
        Author saved = service.create(a);
        URI location = URI.create("/api/author/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Author> update(@PathVariable Long id, @RequestBody Author a) {
        return ResponseEntity.ok(service.update(id,a));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
