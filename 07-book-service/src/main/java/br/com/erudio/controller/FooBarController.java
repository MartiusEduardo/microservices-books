package br.com.erudio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Foo bar")
@RestController
@RequestMapping("book-service")
public class FooBarController {
	
	private Logger logger = LoggerFactory.getLogger(FooBarController.class);
	
	@GetMapping("/foo-bar")
	@Operation(summary = "Foo bar")
	//O @Retry vai tentar fazer a requisição 5 vezes (configurado no application.yml)
	//Para cada tentativa, será esperado apenas 1 segundo para fazer a requisição novamente (configurado no application.yml)
	//Com o exponential-backoff ligado, a espera para fazer a nova requisição vai aumentando exponencialmente.
	//Primeiro espera 1 segundo. Depois 3 segundos. Depois 6 segundos. E assim por diante.
	//Depois das 5 tentativas sem sucesso, vai cair aqui nesse fallbackMethod
//	@Retry(name = "foo-bar", fallbackMethod = "fallbackMethodTeste")
	//O @CircuitBreaker fica fechado permitindo seguir o fluxo normal.
	//Assim que ocorre uma falha na requisição, então o CircuitBreaker fica aberto não permitindo novas requisições.
	//Depois de alguns segundos ele fica meio aberto. O que significa que vai permitir novas requisições que haviam falhado.
	//Caso volte a falhar, então o CircuitBreaker volta a ficar aberto.
	//Caso tenha sucesso, então o CircuitBreaker volta a ficar fechado.
	//Para ficar fechado, o CircuitBreaker espera que todas as requisições estejam funcionando. Se ainda tiver 10% com falha ele volta a ficar aberto.
//	@CircuitBreaker(name = "default", fallbackMethod = "fallbackMethodTeste")
	//O que é o @RateLimiter? Em 10 segundos posso fazer somente 2 requisições. (ver application.yml)
	//Se eu fizer 3 requisições em 10 segundos, na terceira mostra um erro.
	//Depois de alguns segundos eu consigo fazer a requisição novamente sem erro.
//	@RateLimiter(name = "foo-bar")
	//O @Bulkhead determina quantas execuções concorretes eu vou ter.
	@Bulkhead(name = "foo-bar")
	public String fooBar() {
		logger.info("Request to foo-bar is received!");
		var response = new RestTemplate().getForEntity("http://locahost:8080/foo-bar", String.class);
		return response.getBody();
	}
	
	public String fallbackMethodTeste(Exception ex) {
		
		return "fallbackMethod foo-bar!!!";
	}

}
