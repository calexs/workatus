package com.project.workatus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.workatus.model.ProjetoModel;
import com.project.workatus.repository.ProjetoRepository;

import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Objects;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/projeto")
public class ProjetoController {

	private final ProjetoRepository repository;
	DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");

	public ProjetoController(ProjetoRepository repository) {
		this.repository = repository;
	}

	@ApiOperation(value = "Retorna todos os projetos cadastrados")
	@GetMapping("/getAll")
	public List<ProjetoModel> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Retorna um projeto de acordo com o Id")
	@GetMapping("/getId")
	public ProjetoModel getProjetoId(@RequestParam int id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Retorna um projeto de acordo com o Nome")
	@GetMapping("/getNome")
	public ProjetoModel getProjetoNome(@RequestParam String nome) {
		return repository.findByNome(nome);
	}

	@ApiOperation(value = "Insere projeto no sistema")
	@PostMapping("/insert")
	public ProjetoModel insertProjeto(@RequestBody ProjetoModel projeto) {
		boolean nomeExiste = nomeValido(projeto.getNome());

		if (nomeExiste) {
			return null;
		} else {
			if(projeto.getDataFinal().before(projeto.getDataInicio())) {
				return null;
			}
			return repository.save(new ProjetoModel(projeto.getNome(), projeto.getDescricao(), projeto.getDataInicio(),
					projeto.getDataFinal()));
		}
	}

	@ApiOperation(value = "Deleta um projeto de acordo com o Id")
	@DeleteMapping("/deleteId")
	public ResponseEntity<Boolean> deleteProjetoId(@RequestParam int id) {
		ProjetoModel projeto = repository.findById(id);

		if (Objects.isNull(projeto)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}

	@ApiOperation(value = "Deleta um projeto de acordo com o Nome")
	@DeleteMapping("/deleteNome")
	public ResponseEntity<Boolean> deleteProjetoNome(@RequestParam String nome) {
		ProjetoModel projeto = repository.findByNome(nome);

		if (Objects.isNull(projeto)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.deleteById(projeto.getId());
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}

	}

	@ApiOperation(value = "Atualiza as propriedades de um projeto do sistema")
	@PutMapping("/put")
	public ProjetoModel putProjeto(@RequestBody ProjetoModel projeto) {
		boolean idExiste = idValido(projeto.getId());

		if (!idExiste) {
			return null;
		} else {
			ProjetoModel projetoComMesmoNome = repository.findByNome(projeto.getNome());

			if (Objects.isNull(projetoComMesmoNome) || projetoComMesmoNome.getId() == projeto.getId()) {
				ProjetoModel projetoCadastrado = repository.findById(projeto.getId()).get();

				if (projeto.getDataFinal().before(projeto.getDataInicio())) {
					return null;
				} else {
					projetoCadastrado.setNome(projeto.getNome());
					projetoCadastrado.setDescricao(projeto.getDescricao());
					projetoCadastrado.setDataInicio(projeto.getDataInicio());
					projetoCadastrado.setDataFinal(projeto.getDataFinal());
					return repository.save(projetoCadastrado);
				}
			} else {
				return null;
			}
		}
	}

	public boolean idValido(int id) {
		ProjetoModel projeto = repository.findById(id);

		if (Objects.isNull(projeto))
			return false;
		else
			return true;
	}

	public boolean nomeValido(String nome) {
		ProjetoModel projeto = repository.findByNome(nome);

		if (Objects.isNull(projeto))
			return false;
		else
			return true;
	}

}