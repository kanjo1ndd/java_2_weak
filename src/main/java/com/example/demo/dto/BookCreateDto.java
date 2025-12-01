package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookCreateDto {
    @NotBlank
    private String title;
    private Integer year_published;
    private String genre;
    @NotNull
    private Long authorId;
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getYear_published() { return year_published; }
    public void setYear_published(Integer year_published) { this.year_published = year_published; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}
