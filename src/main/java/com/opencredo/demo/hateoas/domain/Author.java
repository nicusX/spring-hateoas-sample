package com.opencredo.demo.hateoas.domain;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.web.util.UriUtils;

@Entity
public class Author extends AbstractPersistable<Long> {
   private static final long serialVersionUID = 4890085128347271414L;

   @Column(unique=true)
   private String handle;
   
   private String firstName;  

   private String lastName;
   
   @ManyToMany(mappedBy="authors")
   private List<Book> books= new ArrayList<>();
   
   public Author() { }
   
   public Author(String firstName, String lastName) {
      
      this.firstName = firstName;
      this.lastName = lastName;
      try {
         this.handle = UriUtils.encodeQueryParam( trimToEmpty(firstName).toLowerCase() + "_" + trimToEmpty(lastName).toLowerCase() , "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
   }
   
   public String getHandle() {
      return handle;
   }

   public void setHandle(String handle) {
      this.handle = handle;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public List<Book> getBooks() {
      return books;
   }
   
}
