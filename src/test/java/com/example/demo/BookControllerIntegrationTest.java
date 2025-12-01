package com.example.demo;

import com.example.demo.dto.BookCreateDto;
import com.example.demo.entity.Author;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthorRepository authorRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private ObjectMapper mapper;

    private Author author;

    @BeforeEach
    void setup() {
        bookRepo.deleteAll();
        authorRepo.deleteAll();
        author = authorRepo.save(new Author("Test Author"));
    }

    // --- CRUD эндпоинты ---
    @SuppressWarnings("null")
    @Test
    void testCreateGetUpdateDeleteBook() throws Exception {
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("Book 1");
        dto.setAuthorId(author.getId());
        dto.setYear_published(2023);
        dto.setGenre("Fiction");

        String content = mapper.writeValueAsString(dto);

        // Create
        String resp = mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Book 1")))
                .andExpect(jsonPath("$.author.name", is(author.getName())))
                .andReturn().getResponse().getContentAsString();

        Long bookId = mapper.readTree(resp).get("id").asLong();

        // Get
        mvc.perform(get("/api/book/" + bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Book 1")));

        // Update
        dto.setTitle("Book Updated");
        mvc.perform(put("/api/book/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Book Updated")));

        // Delete
        mvc.perform(delete("/api/book/" + bookId))
                .andExpect(status().isNoContent());
    }

    // --- _list и _report ---
    @SuppressWarnings("null")
    @Test
    void testListAndReport() throws Exception {
        // создаём 3 книги
        for (int i = 1; i <= 3; i++) {
            BookCreateDto dto = new BookCreateDto();
            dto.setTitle("Book " + i);
            dto.setAuthorId(author.getId());
            dto.setYear_published(2000 + i);
            dto.setGenre("Genre " + i);
            mvc.perform(post("/api/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }

        // --- _list ---
        String listReq = "{\"authorId\":" + author.getId() + ", \"page\":1, \"size\":2}";
        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listReq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list", hasSize(2)))
                .andExpect(jsonPath("$.totalPages", is(2)));

        // --- _report ---
        mvc.perform(post("/api/book/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":" + author.getId() + "}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("report.csv")));
    }

    // --- upload JSON ---
    @SuppressWarnings("null")
    @Test
    void testUploadBooksJson() throws Exception {
        Path path = Path.of("src/main/resources/import/books.json");
        byte[] data = Files.readAllBytes(path);

        MockMultipartFile file = new MockMultipartFile("file", "books.json", "application/json", data);

        mvc.perform(multipart("/api/book/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", is(3)))
                .andExpect(jsonPath("$.failed", is(0)));
    }
}
