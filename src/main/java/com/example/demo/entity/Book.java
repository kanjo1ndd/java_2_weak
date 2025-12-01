package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "book", indexes = {
    @Index(name = "idx_book_year", columnList = "year_published"),
    @Index(name = "idx_book_author", columnList = "author_id")
})
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    private Integer year_published;

    private String genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    public Book() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getYear_published() { return year_published; }
    public void setYear_published(Integer year_published) { this.year_published = year_published; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
}
