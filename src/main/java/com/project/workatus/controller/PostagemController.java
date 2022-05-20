package com.project.workatus.controller;

import org.springframework.web.bind.annotation.*;

import com.project.workatus.model.PostagemModel;
import com.project.workatus.model.TarefaModel;
import com.project.workatus.repository.PostagemRepository;
import com.project.workatus.repository.TarefaRepository;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/postagem")
public class PostagemController {

	private final PostagemRepository repository;
	private final TarefaRepository repositoryTarefa;

	public PostagemController(PostagemRepository repository, TarefaRepository repositoryTarefa) {
		this.repository = repository;
		this.repositoryTarefa = repositoryTarefa;
	}

	@ApiOperation(value = "Retorna todos as postagens cadastradas")
	@GetMapping("/getAll")
	public List<PostagemModel> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Retorna uma postagem de acordo com o Id")
	@GetMapping("/getId")
	public PostagemModel getPostagemId(@RequestParam int id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Insere uma postagem no sistema")
	@PostMapping("/insert")
	public PostagemModel insertPostagem(@RequestBody PostagemModel postagem) {
		boolean idTarefaExiste = idTarefaValido(postagem.getTarefa().getId());

		if (!idTarefaExiste || postagem.getTarefa().getStatus().equals(null)) {
			return null;
		} else {
			Optional<TarefaModel> tarefaOpt = repositoryTarefa.findById(postagem.getTarefa().getId());			
			TarefaModel tarefa = tarefaOpt.get();
			
			tarefa.setStatus(postagem.getTarefa().getStatus());
			tarefa.setPostagem(postagem);
			postagem.setTarefa(tarefa);
			return repository.save(postagem);
		}
	}

	@ApiOperation(value = "Atualiza as propriedades de uma postagem do sistema")
	@PutMapping("/put")
	public PostagemModel putPostagem(@RequestBody PostagemModel postagem) {
		boolean idExiste = idValido(postagem.getId());
		boolean idTarefaExiste = idTarefaValido(postagem.getTarefa().getId());

		if (!idExiste || !idTarefaExiste) {
			return null;
		} else {
			Optional<TarefaModel> tarefaOpt = repositoryTarefa.findById(postagem.getTarefa().getId());			
			TarefaModel tarefa = tarefaOpt.get();
			
			tarefa.setStatus(postagem.getTarefa().getStatus());
			tarefa.setPostagem(postagem);
			postagem.setTarefa(tarefa);
			return repository.save(postagem);
		}
	}

	public boolean idValido(int id) {
		PostagemModel postagem = repository.findById(id);

		if (Objects.isNull(postagem))
			return false;
		else
			return true;
	}

	public boolean idTarefaValido(int id) {
		TarefaModel tarefa = repositoryTarefa.findById(id);

		if (Objects.isNull(tarefa))
			return false;
		else
			return true;
	}
}