package com.opencredo.demo.hateoas.api;

import static com.jayway.restassured.RestAssured.when;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.opencredo.demo.hateoas.domain.Author;
import com.opencredo.demo.hateoas.domain.Book;
import com.opencredo.demo.hateoas.domain.Publisher;

public class ETagSupportTest extends AbstractControllerTest {
   List<Publisher> publishers;
   List<Author> authors;
   List<Book> books;
   
   @Before
   public void setupTestDataset() {
      bookRepository.deleteAll();
      publisherRepository.deleteAll();
      authorRepository.deleteAll();
      
      /// Setup test dataset 
      
      publishers = asList( 
            publisherRepository.save(new Publisher("O'Bryan Publishing"))
      );
      
      authors = asList(
            authorRepository.save(new Author("Walter", "White")),
            authorRepository.save(new Author("Jesse", "Pinkman"))
      );
      
      books = asList(
            bookRepository.save(new Book("45678901", "Cooking at home", asList( authors.get(0), authors.get(1)), publishers.get(0), 1))
      ); 
   }

   @Test
   public void responseContainsEtag() {  
      when().get("/authors/jesse_pinkman").
      then().statusCode(HttpStatus.SC_OK).
      and().contentType(ContentType.JSON).and().header("ETag", notNullValue());
   }
   
   @Test
   public void etagDoesntChangeOnConsecutiveRequestsWithoutChanges() {
      final String etagBefore =
         when().get("/authors/jesse_pinkman").
         then().statusCode(HttpStatus.SC_OK).
         and().extract().header("ETag");
      
      final String etagAfter =
         when().get("/authors/jesse_pinkman").
         then().statusCode(HttpStatus.SC_OK).
         and().extract().header("ETag");
      
      assertEquals(etagBefore, etagAfter);
   }
   
   @Test
   public void etagChangesOnAddingBooks() {
      final String etagBefore =
         when().get("/authors/jesse_pinkman").
         then().statusCode(HttpStatus.SC_OK).
         and().extract().header("ETag");

      // Add a book with the same author,changing the content of the response 
      // as the author resource contains a list of links to authored books
      bookRepository.save(new Book("98765432", "Getting high", asList( authors.get(1) ), publishers.get(0), 1));
      
      final String etagAfter =
         when().get("/authors/jesse_pinkman").
         then().statusCode(HttpStatus.SC_OK).
         and().extract().header("ETag");
      
      assertNotEquals(etagBefore, etagAfter);
   }
}
