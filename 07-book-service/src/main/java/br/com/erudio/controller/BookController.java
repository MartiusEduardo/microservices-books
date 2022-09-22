package br.com.erudio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.model.Book;
import br.com.erudio.proxy.CambioProxy;
import br.com.erudio.repository.BookRepository;
import br.com.erudio.response.Cambio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("book-service")
public class BookController {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private BookRepository repository;
	
	@Autowired
	private CambioProxy proxy;
	
	@Operation(summary = "Find a specific book by your ID")
//	@RouterOperations({
//		@RouterOperation(method = RequestMethod.GET, operation = @Operation(description = "Find a specific book by your ID", operationId = "findBookByIdGET", tags = "Book endpoint"))
//	})
	@GetMapping(value = "/{id}/{currency}")
	public Book findBookById(@PathVariable("id") Long id, @PathVariable("currency") String currency) {
		
		var book = repository.getReferenceById(id);
		if(book == null) throw new RuntimeException("Book not found");
		
		Cambio cambio = proxy.getCambio(book.getPrice(), "USD", currency);
		
		book.setPrice(cambio.getConvertedValue());
		book.setEnvironment(environment.getProperty("local.server.port"));
		
		return book;
	}

}
