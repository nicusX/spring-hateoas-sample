package com.opencredo.demo.hateoas.api.resources;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// Resource class is NOT immutable, to simplify deserialisation using Jackson
@Relation(value="book", collectionRelation="books")
public class BookResource extends ResourceWithEmbeddeds {

   private final String isbn;
   private final String title;
   private final Integer available;
   
   @JsonCreator
   public BookResource(@JsonProperty("isbn") String isbn, @JsonProperty("title") String title, @JsonProperty("available") Integer available) {
      super();
      this.isbn = isbn;
      this.title = title;
      this.available = available;
   }

   public String getIsbn() {
      return isbn;
   }

   public String getTitle() {
      return title;
   }

   public Integer getAvailable() {
      return available;
   }
   
}
