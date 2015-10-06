package com.opencredo.demo.hateoas.api;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.*;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
import com.opencredo.demo.hateoas.domain.Author;

//This is actually an integration test
public class AuthorControllerTest extends AbstractControllerTest {
   List<Author> authors;

   @Before
   public void setupTestDataset() {
      bookRepository.deleteAll();
      publisherRepository.deleteAll();
      authorRepository.deleteAll();
      
      authors = asList(
            authorRepository.save(new Author("David", "Flanagan")),
            authorRepository.save(new Author("Martin", "Fowler")),
            authorRepository.save(new Author("Kendall", "Scott"))
      );
   }   
   
   @Test
   public void canShowOneAuthor() {
      final Author expectedAuthor = authors.get(1);
      
      when().get("/authors/martin_fowler").
      then().statusCode(HttpStatus.SC_OK).and().contentType(ContentType.JSON).
      and().body("firstName", equalTo( expectedAuthor.getFirstName() )).
      and().body("lastName", equalTo( expectedAuthor.getLastName() ));
      
   }
   
   @Test
   public void canCreateNewAuthor() {
      final String newAuthorRequestJson = "{ \"firstName\" : \"Gyro\", \"lastName\" : \"Gearloose\"  }";
      
      final ExtractableResponse<Response> extractable =
            given().contentType(ContentType.JSON).body(newAuthorRequestJson).
            when().post("/authors").
            then().assertThat().statusCode(201).
            and().header("Location", containsString("/authors/") ).
            and().extract();
      
      final String location = extractable.header("Location");      
      assertThat(location, containsString("/authors/"));
      
      final String newAuthorHandle = location.substring(location.lastIndexOf('/') + 1);
      
      final Author actual = authorRepository.findOneByHandle(newAuthorHandle);
      assertEquals("Gyro", actual.getFirstName());
      assertEquals("Gearloose", actual.getLastName());
   }
}
