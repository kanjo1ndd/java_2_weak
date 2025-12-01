package com.example.demo.controller;

import com.example.demo.dto.BookCreateDto;
import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookListRequest;
import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService service;
    public BookController(BookService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<BookDto> create(@Valid @RequestBody BookCreateDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.toDto(service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> update(@PathVariable Long id, @Valid @RequestBody BookCreateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/_list")
    public ResponseEntity<Map<String,Object>> list(@RequestBody BookListRequest req) {
        var page = service.list(req.getAuthorId(), req.getTitle(), req.getPage(), req.getSize());
        var list = page.getContent().stream().map(service::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("list", list, "totalPages", page.getTotalPages()));
    }

    @PostMapping("/_report")
    public void report(@RequestBody BookListRequest req, HttpServletResponse response) throws IOException {
        var page = service.list(req.getAuthorId(), req.getTitle(), 1, Integer.MAX_VALUE);
        List<Book> all = page.getContent();

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv");

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("id", "title", "author", "year", "genre")
                .setSkipHeaderRecord(false)
                .build();

        try (CSVPrinter csv = new CSVPrinter(response.getWriter(), csvFormat)) {
            for (Book b : all) {
                String authorName = b.getAuthor() != null ? b.getAuthor().getName() : "";
                csv.printRecord(
                        b.getId(),
                        b.getTitle(),
                        authorName,
                        b.getYear_published(),
                        b.getGenre()
                );
            }
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String,Object>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        int imported = 0;
        int failed = 0;

        if (file == null || file.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("imported",0,"failed",0));

        File tmp = File.createTempFile("upload","json");
        try (OutputStream os = new FileOutputStream(tmp)) {
            os.write(file.getBytes());
        }

        com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
        try {
            com.fasterxml.jackson.databind.JsonNode root = om.readTree(tmp);
            if (root.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode node : root) {
                    try {
                        String title = node.path("title").asText(null);
                        String authorName = node.path("author").asText(null);
                        Integer year = node.has("year_published") ? node.get("year_published").asInt() : null;
                        String genre = node.path("genre").asText(null);

                        var author = service.createOrFindAuthorByName(authorName);
                        if (author == null) { failed++; continue; }

                        var dto = new BookCreateDto();
                        dto.setTitle(title);
                        dto.setAuthorId(author.getId());
                        dto.setYear_published(year);
                        dto.setGenre(genre);

                        service.create(dto);
                        imported++;
                    } catch (Exception ex) {
                        failed++;
                    }
                }
            }
        } finally {
            tmp.delete();
        }

        return ResponseEntity.ok(Map.of("imported", imported, "failed", failed));
    }
}