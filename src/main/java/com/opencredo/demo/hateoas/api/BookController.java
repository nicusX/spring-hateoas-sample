package com.opencredo.demo.hateoas.api;

import static com.opencredo.demo.hateoas.api.ResourceHandlingUtils.entityOrNotFoundException;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opencredo.demo.hateoas.api.resources.BookPurchase;
import com.opencredo.demo.hateoas.api.resources.BookResource;
import com.opencredo.demo.hateoas.api.resources.NewBook;
import com.opencredo.demo.hateoas.domain.Author;
import com.opencredo.demo.hateoas.domain.Book;
import com.opencredo.demo.hateoas.domain.Publisher;
import com.opencredo.demo.hateoas.domain.persistence.AuthorRepository;
import com.opencredo.demo.hateoas.domain.persistence.BookRepository;
import com.opencredo.demo.hateoas.domain.persistence.PublisherRepository;

@RestController
@ExposesResourceFor(BookResource.class) // This is required to have EntityLinks working
@RequestMapping("/books")
@Transactional // Making the controller transactional is just a way to simplify the persistence implementation (out of scope for this demo)
public class BookController {
   private static Logger LOG = LoggerFactory.getLogger(BookController.class);

   private final BookRepository bookRepository;
   private final AuthorRepository authorRepository;
   private final PublisherRepository publisherRepository;
   
   private final BookResourceAssembler bookResourceAssembler;

   @Autowired
   public BookController(BookRepository bookRepository, AuthorRepository authorRepository, PublisherRepository publisherRepository, BookResourceAssembler bookResourceAssembler) {
      this.bookRepository = bookRepository;
      this.authorRepository = authorRepository;
      this.bookResourceAssembler = bookResourceAssembler;
      this.publisherRepository = publisherRepository;
   }
   
   @RequestMapping(value="/{isbn}", method=RequestMethod.GET)
   public ResponseEntity<BookResource> showBook(
         @PathVariable("isbn") final String isbn, 
         // This is an example of optional details included depending on a query parameter
         @RequestParam(value="detailed", required=false, defaultValue="false") boolean detailed) {
      final Book book = entityOrNotFoundException( bookRepository.findOneByIsbn(isbn) );

      final BookResource resource =  detailed 
            ? bookResourceAssembler.toDetailedResource(book) 
            : bookResourceAssembler.toResource(book);
      LOG.debug("Show Book isbn:{}, detailed:{}", isbn, detailed);
      return ResponseEntity.ok(resource);
      
   }
   
   @RequestMapping(method= RequestMethod.GET)
   public ResponseEntity<Resources<BookResource>> listAllBooks() {
      final Iterable<Book> books = bookRepository.findAll();
      final Resources<BookResource> wrapped = bookResourceAssembler.toEmbeddedList(books);
      LOG.debug("List all books");
      return ResponseEntity.ok(wrapped);
   }
   
   // This simplified version of "list all books" is not HAL compliant, returning an array as top object (it should be _embedded)
//   @RequestMapping(method= RequestMethod.GET)
//   public ResponseEntity<Iterable<BookResource>> listAllBooks() {
//      final Iterable<Book> books = bookRepository.findAll();
//      final Iterable<BookResource> resources = bookResourceAssembler.toResources(books);
//      LOG.debug("List all books");
//      return ResponseEntity.ok(resources);
//   }
   
   
   // Add a book to collection
   @RequestMapping(method=RequestMethod.POST)
   public ResponseEntity<Void> newBook(@RequestBody NewBook newBookRequest) {
      // TODO Add input validation
      
      // Create or retrieve Authors
      final List<Author> authors = new ArrayList<>();
      for(NewBook.Author authorRequest : newBookRequest.getAuthors()) {
         authors.add(createOrRetrieveAuthor(authorRequest));
      }
      
      // Retrieve Publisher (it must exist)
      final Publisher publisher = publisherRepository.findOne( Long.valueOf( newBookRequest.getPublisherId() ) );
      
      final Book savedBook = bookRepository.save( new Book(newBookRequest.getIsbn(), newBookRequest.getTitle(), authors, publisher, 0) ); 
      // No copy, initially
      
      final HttpHeaders headers = new HttpHeaders();
      final Link linkToNewBook = bookResourceAssembler.linkToSingleResource(savedBook);
      headers.add("Location", linkToNewBook.getHref() );
      
      LOG.debug("Book added to collection: {}", linkToNewBook);
      return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
   }
   
   // Purchase copies of the book (an example of "controller" resource)
   @RequestMapping(value="/{isbn}/purchase", method=RequestMethod.PUT)
   public ResponseEntity<Void> purchaseBookCopies(  
         @PathVariable("isbn") final String isbn,
         @RequestBody BookPurchase bookPurchase) {
      // TODO Add input validation

      final Book book = entityOrNotFoundException( bookRepository.findOneByIsbn(isbn) );
      final int newCopiesAvailable = book.getCopiesAvailable() + bookPurchase.getPurchasedCopies();
      book.setCopiesAvailable( newCopiesAvailable );
      bookRepository.save(book);
      
      LOG.debug("Purchased new copies of isbn: {}, now there are {} copies", isbn, newCopiesAvailable); 
      return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
   }
   
   // Borrow a copy of the book (an example of "controller" resource)
   @RequestMapping(value="/{isbn}/borrow-a-copy", method=RequestMethod.PUT)
   public ResponseEntity<Void> borrowACopy( @PathVariable("isbn") final String isbn) {
      final Book book = entityOrNotFoundException( bookRepository.findOneByIsbn(isbn) );
      
      // If there is no copy available, return a 400/Bad Request
      // with a custom header containing the error
      if ( book.getCopiesAvailable() <= 0 ) {
         LOG.debug("No copy available for borrowing isbn: {}", isbn);
         final HttpHeaders headers = new HttpHeaders();
         headers.add("X-Error", "No copy available");
         return new ResponseEntity<Void>(headers, HttpStatus.BAD_REQUEST);
      }
      
      book.setCopiesAvailable( book.getCopiesAvailable() - 1 );
      bookRepository.save(book);
      LOG.debug("Borrowed a copy of isbn:{}; {} remaining", isbn, book.getCopiesAvailable() ); 
         
      return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
   }
   
   // Return a copy of the book
   @RequestMapping(value="/{isbn}/return-a-copy", method=RequestMethod.PUT)
   public ResponseEntity<Void> returnACopy( @PathVariable("isbn") final String isbn) {
      final Book book = entityOrNotFoundException( bookRepository.findOneByIsbn(isbn) );
      
      book.setCopiesAvailable( book.getCopiesAvailable() +1 );
      bookRepository.save(book);
      LOG.debug("Returned a copy of isbn:{}; {} available now", isbn, book.getCopiesAvailable() ); 
         
      return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
      
   }
   
   private Author createOrRetrieveAuthor(NewBook.Author newAuthorRequest) {
      final Author candidateNewAuthor = new Author( newAuthorRequest.getFirstName(), newAuthorRequest.getLastName() );
      final Author existingAuthor = authorRepository.findOneByHandle( candidateNewAuthor.getHandle() );
      return existingAuthor != null ? existingAuthor : authorRepository.save(candidateNewAuthor);
   }
   
   @RequestMapping(value="/{isbn}", method=RequestMethod.DELETE)
   public ResponseEntity<Void> deleteBook( @PathVariable("isbn") final String isbn) {
      final Book bookToDelete = bookRepository.findOneByIsbn(isbn);
      if ( bookToDelete != null ) {
         LOG.debug("Removed from book collection isbn:{}", isbn);
         bookRepository.delete(bookToDelete);
      }
      // Note this return 204/No Content regardless the book has actually been deleted or not, to be idempotent
      return ResponseEntity.noContent().build();
   }
   
}
