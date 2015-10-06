package com.opencredo.demo.hateoas.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Book extends AbstractPersistable<Long> {
   private static final long serialVersionUID = 298443116673285961L;

   @Column(unique=true)
   private String isbn;
   
   private String title;
   
   private int copiesAvailable;
   
   @ManyToMany(fetch=FetchType.EAGER)
   @JoinTable(name="books_authors", 
      joinColumns={@JoinColumn(name="book_id", referencedColumnName="id")},
      inverseJoinColumns={@JoinColumn(name="author_id", referencedColumnName="id")}
   )
   private List<Author> authors = new ArrayList<>();
   
   @ManyToOne(fetch=FetchType.EAGER)
   @JoinColumn(name="publisher_id")
   private Publisher publisher;
   
   protected Book() { 
   }
   
   public Book(final String isbn, final String title, final List<Author> authors, Publisher publisher, int copiesAvailable) {
      this.isbn = isbn;
      this.title = title;
      this.authors = authors;
      this.publisher = publisher;
      this.copiesAvailable = copiesAvailable;
   }

   public String getIsbn() {
      return isbn;
   }

   public String getTitle() {
      return title;
   }

   public List<Author> getAuthors() {
      return authors;
   }

   public Publisher getPublisher() {
      return publisher;
   }

   public int getCopiesAvailable() {
      return copiesAvailable;
   }

   public void setCopiesAvailable(int copiesAvailable) {
      this.copiesAvailable = copiesAvailable;
   }

 }
