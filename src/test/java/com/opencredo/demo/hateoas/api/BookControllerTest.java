package com.opencredo.demo.hateoas.api;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
import com.opencredo.demo.hateoas.api.resources.AuthorResource;
import com.opencredo.demo.hateoas.api.resources.BookResource;
import com.opencredo.demo.hateoas.api.resources.PublisherResource;
import com.opencredo.demo.hateoas.domain.Author;
import com.opencredo.demo.hateoas.domain.Book;
import com.opencredo.demo.hateoas.domain.Publisher;
// This is actually an integration test starting the full application
public class BookControllerTest extends AbstractControllerTest {
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
            publisherRepository.save(new Publisher("O'Reilly")),
            publisherRepository.save(new Publisher("Addison Wesley"))
      );
      
      authors = asList(
            authorRepository.save(new Author("David", "Flanagan")),
            authorRepository.save(new Author("Martin", "Fowler")),
            authorRepository.save(new Author("Kendall", "Scott"))
      );
      
      
      books = asList(
            bookRepository.save(new Book("0596007736", "Java in a Nutshell", asList( authors.get(0)  ), publishers.get(0), 3)  ),
            bookRepository.save(new Book("0321127420", "Patterns of Enterprise Application Architecture", asList( authors.get(1), authors.get(2)), publishers.get(1), 1)),
            bookRepository.save(new Book("0321193687", "UML Distilled: Applying the Standard Object Modelling Language", asList( authors.get(1)), publishers.get(1), 0))
      ); 
   }
   
   @Test
   public void canListAll() {
       
      
      when().get("/books").      
      then().statusCode(HttpStatus.SC_OK).and().contentType(ContentType.JSON).
      and().body("_embedded.books", hasSize(3) ).
      and().body("_embedded.books.isbn", hasItems(  books.stream().map( Book::getIsbn ).toArray() ));
   }
   
   @Test 
   public void canShowOneBook() {
      final Book expectedBook = books.get(0);
      
      when().get("/books/" + expectedBook.getIsbn()).
      then().statusCode(HttpStatus.SC_OK).and().contentType(ContentType.JSON).
      and().body("isbn", equalTo( expectedBook.getIsbn() )).
      and().body("title", equalTo( expectedBook.getTitle() )).
      and().body("available", equalTo( expectedBook.getCopiesAvailable()));
   }
   
   @Test
   public void canTraverseBookSelfLink() throws Exception {
      final Book expectedBook = books.get(0);
      final ParameterizedTypeReference<Resource<BookResource>> resourceParameterizedTypeReference = new ParameterizedTypeReference<Resource<BookResource>>() {};

      
      final Resource<BookResource> actual = new Traverson(new URI("http://localhost:" + port + "/books/" + expectedBook.getIsbn()), MediaTypes.HAL_JSON).
            follow("self").toObject(resourceParameterizedTypeReference);
      
      assertNotNull(actual.getContent());
      assertEquals(expectedBook.getIsbn(), actual.getContent().getIsbn());
   }

   @Test
   public void canTraverseBookPublisherLink() throws Exception {
      final Book expectedBook = books.get(0);
      final ParameterizedTypeReference<Resource<PublisherResource>> resourceParameterizedTypeReference = new ParameterizedTypeReference<Resource<PublisherResource>>() {};
      
      final Resource<PublisherResource> actual = new Traverson(new URI("http://localhost:" + port + "/books/" + expectedBook.getIsbn()), MediaTypes.HAL_JSON).
            follow( "publisher" ).toObject(resourceParameterizedTypeReference);
      
      assertNotNull(actual.getContent());
      assertEquals(expectedBook.getPublisher().getName(), actual.getContent().getName());
   }
   

   @Test
   public void canTraverseOneOfBookAuthorLinks() throws Exception {
      final Book expectedBook = books.get(0);
      final ParameterizedTypeReference<Resource<AuthorResource>> resourceParameterizedTypeReference = new ParameterizedTypeReference<Resource<AuthorResource>>() {};


     // Note the current version of Traverson implementation doesn't support CURIes properly
      final Resource<AuthorResource> actual = new Traverson(new URI("http://localhost:" + port + "/books/" + expectedBook.getIsbn()), MediaTypes.HAL_JSON).
            follow("authors[0]").toObject(resourceParameterizedTypeReference);
      
      assertNotNull(actual.getContent());
      assertEquals(expectedBook.getAuthors().get(0).getFirstName(), actual.getContent().getFirstName());
      assertEquals(expectedBook.getAuthors().get(0).getLastName(), actual.getContent().getLastName());
   }
   
   @Test
   public void canAddABookToCollection() {
      
      final Long publisherId = publishers.get(0).getId();
      final String newBookJson = "{ \"isbn\" : \"1324354657\", \"title\" : \"A New Book\", \"publisherId\" : " + publisherId + ", "
            + " \"authors\" : [ { \"firstName\" : \"New\", \"lastName\" : \"Author\"  } ] }";
      
      final ExtractableResponse<Response> extractable =
            given().contentType(ContentType.JSON).body(newBookJson).
            when().post("/books").
            then().assertThat().statusCode(201).
            and().extract();
      
      final String location = extractable.header("Location");
      assertThat(location, containsString("/books/"));
      final String newBookIsbn = location.substring(location.lastIndexOf('/') + 1);
      
      assertEquals("1324354657", newBookIsbn);
     
      final Book actual = bookRepository.findOneByIsbn(newBookIsbn);
      assertEquals("A New Book", actual.getTitle());
      assertEquals("1324354657", actual.getIsbn());
      assertThat(actual.getAuthors(), hasSize(1));
      assertEquals("New", actual.getAuthors().get(0).getFirstName());
      assertEquals("Author", actual.getAuthors().get(0).getLastName());
   }
   
   @Test
   public void canPurchaseNewCopiesOfABook() {
      final String isbn = books.get(0).getIsbn();

      final int orginalCopies = bookRepository.findOneByIsbn(isbn).getCopiesAvailable();
      
      final String bookPurchaseJson = "{ \"purchasedCopies\" : 3  }";
     
      given().contentType(ContentType.JSON).body(bookPurchaseJson).
      when().put("/books/" + isbn + "/purchase").
      then().assertThat().statusCode(204);
      
      final int actualUpdatedCopies = bookRepository.findOneByIsbn(isbn).getCopiesAvailable();
      assertEquals(orginalCopies + 3, actualUpdatedCopies );
   }
   
   @Test
   public void canBorrowACopy() {
      final Book bookWithCopies = books.get(0);
      final String isbn = bookWithCopies.getIsbn();
      final int originalCopies = bookWithCopies.getCopiesAvailable();
      
      given().contentType(ContentType.JSON).
      when().put("/books/" + isbn + "/borrow-a-copy").
      then().assertThat().statusCode(204);
      
      final int actualUpdatedCopies = bookRepository.findOneByIsbn(isbn).getCopiesAvailable();
      assertEquals(originalCopies - 1, actualUpdatedCopies );     
   }
   
   @Test
   public void cannotBorrowWhenNoCopyAvailable() {
      final Book bookWithNoCopy = books.get(2);
      final String isbn = bookWithNoCopy.getIsbn();
      

      given().contentType(ContentType.JSON).
      when().put("/books/" + isbn + "/borrow-a-copy").
      then().assertThat().statusCode(400);
   
      final int actualUpdatedCopies = bookRepository.findOneByIsbn(isbn).getCopiesAvailable();
      assertEquals(0, actualUpdatedCopies );           
   }
}
