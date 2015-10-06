package com.opencredo.demo.hateoas.api.resources;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Relation(value="author", collectionRelation="authors")
public class AuthorResource extends ResourceWithEmbeddeds {

   private final String firstName;
   private final String lastName;

   @JsonCreator
   public AuthorResource(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName) {
      super();
      this.firstName = firstName;
      this.lastName = lastName;
   }
   
   public String getFirstName() {
      return firstName;
   }   
   public String getLastName() {
      return lastName;
   }
   
}
