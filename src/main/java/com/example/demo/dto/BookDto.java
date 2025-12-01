package com.example.demo.dto;

public class BookDto {
    private Long id;
    private String title;
    private Integer year_published;
    private String genre;
    private AuthorDto author;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getYear_published() { return year_published; }
    public void setYear_published(Integer year_published) { this.year_published = year_published; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public AuthorDto getAuthor() { return author; }
    public void setAuthor(AuthorDto author) { this.author = author; }
}
