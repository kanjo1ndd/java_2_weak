package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("""
        select b from Book b
        where (:authorId is null or b.author.id = :authorId)
        and (coalesce(:title, '') = '' or lower(b.title) like lower(concat('%', :title, '%')))
    """)
    Page<Book> listFiltered(@Param("authorId") Long authorId, @Param("title") String title, Pageable pageable);
}
