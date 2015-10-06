package com.opencredo.demo.hateoas.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Publisher extends AbstractPersistable<Long> {
   private static final long serialVersionUID = 8365902915574146481L;

   private String name;
   
   @OneToMany(mappedBy="publisher")
   private List<Book> books = new ArrayList<>();
   
   protected Publisher() {
   }
   
   public Publisher(String name) {
      this.name = name;
   }
   
   public String getName() {
      return name;
   }

   public List<Book> getBooks() {
      return books;
   }
   
}
