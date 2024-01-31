package com.plasmus777.farmacia.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.plasmus777.farmacia.repository.CategoriaRepository;
import com.plasmus777.farmacia.repository.ProdutoRepository;
import com.plasmus777.farmacia.model.Produto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@GetMapping
	public ResponseEntity<List<Produto>> getAll(){
		return ResponseEntity.ok(produtoRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Produto> getById(@PathVariable Long id){
		return produtoRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Produto>> getByNome(@PathVariable String nome){
		return ResponseEntity.ok(produtoRepository.findAllByNomeContainingIgnoreCase(nome));
	}
	
	@GetMapping("/descricao/{descricao}")
	public ResponseEntity<List<Produto>> getByDescricao(@PathVariable String descricao){
		return ResponseEntity.ok(produtoRepository.findAllByDescricaoContainingIgnoreCase(descricao));
	}
	
	@GetMapping("/preco_max/{preco}")
	public ResponseEntity<List<Produto>> getByPrecoMenorOuIgual(@PathVariable Double preco) {
		return ResponseEntity.ok(produtoRepository.findByPrecoLessThanEqual(preco));
	}

	@GetMapping("/preco_entre/{precoMin}/{precoMax}")
	public ResponseEntity<List<Produto>> getByPrecoEntre(@PathVariable Double precoMin, @PathVariable Double precoMax) {
		return ResponseEntity.ok(produtoRepository.findByPrecoBetween(precoMin, precoMax));
	}
	
	@PostMapping
	public ResponseEntity<Produto> post(@Valid @RequestBody Produto Produto){
		if(categoriaRepository.existsById(Produto.getCategoria().getId()))
			return ResponseEntity.status(HttpStatus.CREATED).body(produtoRepository.save(Produto));
		
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A categoria do produto não existe!", null);
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		
		if(produto.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		produtoRepository.deleteById(id);
	}
	
	@PutMapping
	public ResponseEntity<Produto> put(@Valid @RequestBody Produto Produto){
		if(produtoRepository.existsById(Produto.getId())) {
			if(categoriaRepository.existsById(Produto.getCategoria().getId()))
				return ResponseEntity.status(HttpStatus.OK)
						.body(produtoRepository.save(Produto));
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A categoria do produto não existe!", null);
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
}
