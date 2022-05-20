package com.project.workatus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.workatus.model.PostagemModel;
import com.project.workatus.model.ProjetoModel;
import com.project.workatus.model.TarefaModel;
import com.project.workatus.model.UsuarioModel;
import com.project.workatus.model.enums.EnumCargo;
import com.project.workatus.model.enums.EnumStatus;
import com.project.workatus.repository.ProjetoRepository;
import com.project.workatus.repository.TarefaRepository;
import com.project.workatus.repository.UsuarioRepository;

import io.swagger.annotations.ApiOperation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/tarefa")
public class TarefaController {

	private final TarefaRepository repository;
	private final UsuarioRepository repositoryUsuario;
	private final ProjetoRepository repositoryProjeto;
	java.sql.Date dataAtual = new java.sql.Date(Calendar.getInstance().getTime().getTime());

	public TarefaController(TarefaRepository repository, UsuarioRepository repositoryUsuario, ProjetoRepository repositoryProjeto) {
		this.repository = repository;
		this.repositoryUsuario = repositoryUsuario;
		this.repositoryProjeto = repositoryProjeto;
	}

	@ApiOperation(value = "Retorna todas as tarefas cadastradas")
	@GetMapping("/getAll")
	public List<TarefaModel> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Retorna uma tarefa de acordo com o Id")
	@GetMapping("/getId")
	public TarefaModel getTarefaId(@RequestParam int id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Retorna uma tarefa de acordo com o Titulo")
	@GetMapping("/getTitulo")
	public TarefaModel getTarefaTitulo(@RequestParam String titulo) {
		return repository.findByTitulo(titulo);
	}
	
	@ApiOperation(value = "Retorna a lista de postagens desta Tarefa")
	@GetMapping("/getPostagens")
	public List<PostagemModel> getPostagensId(@RequestParam int id) {
		return repository.findById(id).getPostagens();
	}

	@ApiOperation(value = "Insere uma tarefa no sistema")
	@PostMapping("/insert")
	public TarefaModel insertTarefa(@RequestBody TarefaModel tarefa) {		
		boolean tituloExiste = tituloValido(tarefa.getTitulo());
		boolean idUsuarioAdministradorExiste = idUsuarioValido(tarefa.getUsuarioAdministrador().getId());
		boolean idUsuarioFuncionarioExiste = idUsuarioValido(tarefa.getUsuarioFuncionario().getId());
		boolean idProjetoExiste = idProjetoValido(tarefa.getProjeto().getId());

		if (tituloExiste || !idUsuarioAdministradorExiste || !idUsuarioFuncionarioExiste || !idProjetoExiste) {
			return null;
		} else {		
			if (tarefa.getDataFinal().before(tarefa.getDataInicio())) {
				return null;
			}

			Optional<UsuarioModel> usuarioAdministrador = repositoryUsuario.findById(tarefa.getUsuarioAdministrador().getId());
			UsuarioModel usuarioAdm = usuarioAdministrador.get();
			tarefa.setUsuarioAdministrador(usuarioAdm);
			
			if(!tarefa.getUsuarioAdministrador().getCargo().equals(EnumCargo.Administrador)) {
				return null;
			}
			
			Optional<UsuarioModel> usuarioFuncionario = repositoryUsuario.findById(tarefa.getUsuarioFuncionario().getId());
			UsuarioModel usuarioFunc = usuarioFuncionario.get();	
			tarefa.setUsuarioFuncionario(usuarioFunc);			
			
			Optional<ProjetoModel> projetoOpt = repositoryProjeto.findById(tarefa.getProjeto().getId());
			ProjetoModel projeto = projetoOpt.get();
			
			
			tarefa.setDataCadastro(dataAtual);
			tarefa.setDataFinal(formataData(tarefa.getDataFinal()));
			tarefa.setDataInicio(formataData(tarefa.getDataInicio()));
			tarefa.setDescricao(tarefa.getDescricao());
			if (Objects.isNull(tarefa.getStatus())) {
				tarefa.setStatus(EnumStatus.Pendente);
			} else {
				tarefa.setStatus(tarefa.getStatus());
			}
			tarefa.setTitulo(tarefa.getTitulo());
			tarefa.setProjeto(projeto);
			projeto.setTarefa(tarefa);
			tarefa.getProjeto().setFuncionario(usuarioFunc);
			return repository.save(tarefa);
		}
	}

	@ApiOperation(value = "Deleta uma tarefa de acordo com o Id")
	@DeleteMapping("/deleteId")
	public ResponseEntity<Boolean> deleteTarefaId(@RequestParam int id) {
		TarefaModel tarefa = repository.findById(id);

		if (Objects.isNull(tarefa)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}

	@ApiOperation(value = "Deleta uma tarefa de acordo com o Titulo")
	@DeleteMapping("/deleteTitulo")
	public ResponseEntity<Boolean> deleteTarefaTitulo(@RequestParam String titulo) {
		TarefaModel tarefa = repository.findByTitulo(titulo);

		if (Objects.isNull(tarefa)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.deleteById(tarefa.getId());
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}

	}

	@ApiOperation(value = "Atualiza as propriedades de uma tarefa do sistema")
	@PutMapping("/put")
	public TarefaModel putTarefa(@RequestBody TarefaModel tarefa) {
		boolean idExiste = idTarefaValido(tarefa.getId());
		boolean idUsuarioAdministradorExiste = idUsuarioValido(tarefa.getUsuarioAdministrador().getId());
		boolean idUsuarioFuncionarioExiste = idUsuarioValido(tarefa.getUsuarioFuncionario().getId());
		boolean idProjetoExiste = idProjetoValido(tarefa.getProjeto().getId());

		if (!idExiste || !idUsuarioAdministradorExiste || !idUsuarioFuncionarioExiste || !idProjetoExiste) {
			return null;
		} else {
			TarefaModel tarefaComMesmoTitulo = repository.findByTitulo(tarefa.getTitulo());

			if (Objects.isNull(tarefaComMesmoTitulo) || tarefaComMesmoTitulo.getId() == tarefa.getId()) {
				TarefaModel tarefaCadastrada = repository.findById(tarefa.getId()).get();

				if (tarefa.getDataFinal().before(tarefa.getDataInicio())) {
					return null;
				} else {													
					Optional<UsuarioModel> usuarioAdministrador = repositoryUsuario.findById(tarefa.getUsuarioAdministrador().getId());
					UsuarioModel usuarioAdm = usuarioAdministrador.get();
					tarefaCadastrada.setUsuarioAdministrador(usuarioAdm);
										
					if(!tarefaCadastrada.getUsuarioAdministrador().getCargo().equals(EnumCargo.Administrador)) {
						return null;
					}
					
					Optional<UsuarioModel> usuarioFuncionario = repositoryUsuario.findById(tarefa.getUsuarioFuncionario().getId());
					UsuarioModel usuarioFunc = usuarioFuncionario.get();
					tarefaCadastrada.setUsuarioFuncionario(usuarioFunc);
										
					Optional<ProjetoModel> projetoOpt = repositoryProjeto.findById(tarefa.getProjeto().getId());
					ProjetoModel projeto = projetoOpt.get();				
					
					tarefaCadastrada.setDataCadastro(dataAtual);
					tarefaCadastrada.setDataFinal(formataData(tarefa.getDataFinal()));
					tarefaCadastrada.setDataInicio(formataData(tarefa.getDataInicio()));
					tarefaCadastrada.setDescricao(tarefa.getDescricao());
					if (!Objects.isNull(tarefa.getStatus())) {
						tarefaCadastrada.setStatus(tarefa.getStatus());
					}
					tarefaCadastrada.setTitulo(tarefa.getTitulo());
					tarefaCadastrada.setProjeto(projeto);					
					return repository.save(tarefaCadastrada);
				}
			} else {
				return null;
			}
		}
	}

	public boolean idTarefaValido(int id) {
		TarefaModel tarefa = repository.findById(id);

		if (Objects.isNull(tarefa))
			return false;
		else
			return true;
	}
	
	public boolean idUsuarioValido(int id) {
		UsuarioModel usuario = repositoryUsuario.findById(id);

		if (Objects.isNull(usuario))
			return false;
		else
			return true;
	}
	
	public boolean idProjetoValido(int id) {
		ProjetoModel projeto = repositoryProjeto.findById(id);

		if (Objects.isNull(projeto))
			return false;
		else
			return true;
	}

	public boolean tituloValido(String titulo) {
		TarefaModel tarefa = repository.findByTitulo(titulo);

		if (Objects.isNull(tarefa))
			return false;
		else
			return true;
	}

	public java.sql.Date formataData(java.sql.Date data) {
		LocalDate dataLocal = data.toLocalDate();
		LocalDate dataCorreta = dataLocal.plusDays(1);
		Date dataRetorno = Date.from(dataCorreta.atStartOfDay(ZoneId.systemDefault()).toInstant());
		java.sql.Date dataSQL = new java.sql.Date(dataRetorno.getTime());
		return dataSQL;
	}

}
