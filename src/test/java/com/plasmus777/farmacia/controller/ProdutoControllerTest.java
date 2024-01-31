package com.plasmus777.farmacia.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.plasmus777.farmacia.model.Categoria;
import com.plasmus777.farmacia.model.Produto;
import com.plasmus777.farmacia.repository.CategoriaRepository;
import com.plasmus777.farmacia.repository.ProdutoRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProdutoControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	private Categoria c1, c2, c3;
	
	@BeforeAll
	void start(){
		produtoRepository.deleteAll();
		
		c1 = categoriaRepository.save(new Categoria(0L, "Categoria de testes 1", "Isso está aqui para testar o programa"));
		c2 = categoriaRepository.save(new Categoria(0L, "Categoria de testes 2", "Isso está aqui para testar o programa"));
		c3 = categoriaRepository.save(new Categoria(0L, "Categoria Qualquer 3", "Categoria aleatória"));
		
		produtoRepository.save(new Produto(0L, "Produto de Testes 1", "Isso é um produto criado para testes", 10.99, 12, c1));
		produtoRepository.save(new Produto(0L, "Produto de Testes 2", "Isso é um produto criado para testes", 20.99, 15, c3));
		produtoRepository.save(new Produto(0L, "Produto de Testes 3", "Isso é um produto criado para testes", 30.99, 13, c2));
		produtoRepository.save(new Produto(0L, "Produto Qualquer 1", "Produto aleatório", 40.99, 11, c2));
		produtoRepository.save(new Produto(0L, "Produto Qualquer 2", "Produto aleatório", 50.99, 14, c3));
		produtoRepository.save(new Produto(0L, "Produto Qualquer 3", "Produto aleatório", 60.99, 16, c1));
	}
	
	@Test
	@DisplayName("Criar um produto")
	public void deveCriarUmProduto() {
		HttpEntity<Produto> corpoRequisicao = new HttpEntity<Produto>(new Produto(0L, "Produto de Testes 75", "Isso é um produto criado para testes", 10.99, 12, c2));
		
		ResponseEntity<Produto> corpoResposta = testRestTemplate.exchange("/produtos", HttpMethod.POST, corpoRequisicao, Produto.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualização de um produto")
	public void deveAtualizarProduto() {
		Produto produtoExistente = produtoRepository.save(new Produto(0L, "Produto de Testes 100", "Isso é um produto criado para testes", 15.99, 12, c3));
		
		Produto produtoNovo = new Produto(produtoExistente.getId(), "Produto de Testes 200", "Isso é um produto criado para testes", 25.99, 7, c1);
		
		HttpEntity<Produto> corpoRequisicao = new HttpEntity<Produto>(produtoNovo);
		
		ResponseEntity<Produto> corpoResposta = testRestTemplate
				.exchange("/produtos", HttpMethod.PUT, corpoRequisicao, Produto.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os produtos")
	public void deveListarTodasOsProdutos() {
		produtoRepository.save(new Produto(0L, "Produto de Testes 51", "Isso é um produto criado para testes", 10.99, 6, c1));
		produtoRepository.save(new Produto(0L, "Produto de Testes 61", "Isso é um produto criado para testes", 15.99, 4, c3));
		
		ResponseEntity<String> resposta = testRestTemplate
				.exchange("/produtos", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Buscar produtos por nome")
	public void deveBuscarProdutosPorNome() {
		produtoRepository.save(new Produto(0L, "Produto de Testes 71", "Isso é um produto criado para testes", 10.99, 32, c2));
		produtoRepository.save(new Produto(0L, "Produto Qualquer 81", "Isso é um produto qualquer", 16.99, 24, c2));
		
		ResponseEntity<String> resposta = testRestTemplate
				.exchange("/produtos/nome/Qualquer", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Buscar produtos por descricao")
	public void deveBuscarProdutosPorDescricao() {
		produtoRepository.save(new Produto(0L, "Produto de Testes 91", "Isso é um produto criado para testes", 10.99, 32, c1));
		produtoRepository.save(new Produto(0L, "Produto Qualquer 101", "Isso é um produto qualquer", 16.99, 24, c2));
		
		ResponseEntity<String> resposta = testRestTemplate
				.exchange("/produtos/descricao/criado", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Apagar produto por ID")
	public void deveApagarProdutoPorId() {
		Produto produto = produtoRepository.save(new Produto(0L, "Produto de Testes 256", "Isso é um produto criado para testes", 14.99, 22, c3));
		
		ResponseEntity<String> resposta = testRestTemplate
				.exchange("/produtos/" + produto.getId(), HttpMethod.DELETE, null, String.class);
		
		assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Buscar produtos por preço máximo")
	public void deveBuscarProdutosPorPrecoMax() {
		produtoRepository.save(new Produto(0L, "Produto de Testes 91", "Isso é um produto criado para testes", 10.99, 32, c3));
		produtoRepository.save(new Produto(0L, "Produto Qualquer 101", "Isso é um produto qualquer", 16.99, 24, c2));
		
		ResponseEntity<String> resposta = testRestTemplate
				.exchange("/produtos/preco_max/11", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Buscar produtos por preço entre dois valores")
	public void deveBuscarProdutosPorPrecoEntre() {
		produtoRepository.save(new Produto(0L, "Produto de Testes 91", "Isso é um produto criado para testes", 10.99, 32, c2));
		produtoRepository.save(new Produto(0L, "Produto Qualquer 101", "Isso é um produto qualquer", 16.99, 24, c1));
		
		ResponseEntity<String> resposta = testRestTemplate
				.exchange("/produtos/preco_entre/11/21", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
}