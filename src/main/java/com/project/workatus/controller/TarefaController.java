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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	long millis = System.currentTimeMillis();
	java.sql.Date dataAtual = new java.sql.Date(millis);

	public TarefaController(TarefaRepository repository, UsuarioRepository repositoryUsuario,
			ProjetoRepository repositoryProjeto) {
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
		TarefaModel cadastroTarefa = new TarefaModel();

		if (Objects.isNull(tarefa.getTitulo()) || Objects.isNull(tarefa.getUsuarioAdministrador().getId())
				|| Objects.isNull(tarefa.getUsuarioFuncionario().getId()) || Objects.isNull(tarefa.getProjeto().getId())
				|| Objects.isNull(tarefa.getDataFinal()) || Objects.isNull(tarefa.getDataInicio())) {
			return null;
		}

		boolean tituloExiste = tituloValido(tarefa.getTitulo());
		boolean idUsuarioAdministradorExiste = idUsuarioValido(tarefa.getUsuarioAdministrador().getId());
		boolean idUsuarioFuncionarioExiste = idUsuarioValido(tarefa.getUsuarioFuncionario().getId());
		boolean idProjetoExiste = idProjetoValido(tarefa.getProjeto().getId());

		if (tituloExiste || !idUsuarioAdministradorExiste || !idUsuarioFuncionarioExiste || !idProjetoExiste
				|| tarefa.getDataFinal().before(tarefa.getDataInicio())) {
			return null;
		} else {
			Optional<UsuarioModel> usuarioAdministrador = repositoryUsuario
					.findById(tarefa.getUsuarioAdministrador().getId());
			UsuarioModel usuarioAdm = usuarioAdministrador.get();

			if (!usuarioAdm.getCargo().equals(EnumCargo.Administrador))
				return null;
			else
				cadastroTarefa.setUsuarioAdministrador(usuarioAdm);

			Optional<UsuarioModel> usuarioFuncionario = repositoryUsuario
					.findById(tarefa.getUsuarioFuncionario().getId());
			UsuarioModel usuarioFunc = usuarioFuncionario.get();
			cadastroTarefa.setUsuarioFuncionario(usuarioFunc);

			Optional<ProjetoModel> projetoOpt = repositoryProjeto.findById(tarefa.getProjeto().getId());
			ProjetoModel projeto = projetoOpt.get();
			cadastroTarefa.setProjeto(projeto);

			if (!projeto.getFuncionarios().contains(usuarioFunc))
				projeto.setFuncionarios(usuarioFunc);

			if (!usuarioFunc.getProjetos().contains(projeto))
				usuarioFunc.setProjetos(projeto);

			cadastroTarefa.setDataCadastro(dataAtual);
			cadastroTarefa.setDataFinal(formataData(tarefa.getDataFinal()));
			cadastroTarefa.setDataInicio(formataData(tarefa.getDataInicio()));
			cadastroTarefa.setDescricao(tarefa.getDescricao());
			if (Objects.isNull(tarefa.getStatus()))
				cadastroTarefa.setStatus(EnumStatus.Pendente);
			else
				cadastroTarefa.setStatus(tarefa.getStatus());

			cadastroTarefa.setTitulo(tarefa.getTitulo());
			projeto.setTarefas(cadastroTarefa);

			usuarioFunc.setTarefasAtribuidas(cadastroTarefa);
			usuarioAdm.setTarefasCadastradas(cadastroTarefa);
			return repository.save(cadastroTarefa);
		}
	}

	@ApiOperation(value = "Deleta uma tarefa de acordo com o Id")
	@DeleteMapping("/deleteId")
	public ResponseEntity<Boolean> deleteTarefaId(@RequestParam int id) {
		TarefaModel tarefa = repository.findById(id);

		if (Objects.isNull(tarefa)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {			
			List<ProjetoModel> projetos = repositoryProjeto.findAll();
			for (ProjetoModel projeto : projetos) {
				if (projeto.getTarefas().contains(tarefa)) {
					projeto.getTarefas().remove(tarefa);
				}
				if(projeto.getFuncionarios().contains(tarefa.getUsuarioFuncionario())) {
					projeto.getFuncionarios().remove(tarefa.getUsuarioFuncionario());
				}
			}

			List<UsuarioModel> usuariosFunc = repositoryUsuario.findAll();
			for (UsuarioModel usuarioFunc : usuariosFunc) {
				if (usuarioFunc.getTarefasAtribuidas().contains(tarefa)) {
					usuarioFunc.getTarefasAtribuidas().remove(tarefa);
				}
				if(usuarioFunc.getProjetos().contains(tarefa.getProjeto())) {
					usuarioFunc.getProjetos().remove(tarefa.getProjeto());
				}
			}

			List<UsuarioModel> usuariosAdm = repositoryUsuario.findAll();
			for (UsuarioModel usuarioAdm : usuariosAdm) {
				if (usuarioAdm.getTarefasCadastradas().contains(tarefa)) {
					usuarioAdm.getTarefasCadastradas().remove(tarefa);
				} else if (usuarioAdm.getTarefasAtribuidas().contains(tarefa)) {
					usuarioAdm.getTarefasAtribuidas().remove(tarefa);
				}
			}
			
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
			List<ProjetoModel> projetos = repositoryProjeto.findAll();
			for (ProjetoModel projeto : projetos) {
				if (projeto.getTarefas().contains(tarefa)) {
					projeto.getTarefas().remove(tarefa);
				}
				if(projeto.getFuncionarios().contains(tarefa.getUsuarioFuncionario())) {
					projeto.getFuncionarios().remove(tarefa.getUsuarioFuncionario());
				}
			}

			List<UsuarioModel> usuariosFunc = repositoryUsuario.findAll();
			for (UsuarioModel usuarioFunc : usuariosFunc) {
				if (usuarioFunc.getTarefasAtribuidas().contains(tarefa)) {
					usuarioFunc.getTarefasAtribuidas().remove(tarefa);
				}
				if(usuarioFunc.getProjetos().contains(tarefa.getProjeto())) {
					usuarioFunc.getProjetos().remove(tarefa.getProjeto());
				}
			}

			List<UsuarioModel> usuariosAdm = repositoryUsuario.findAll();
			for (UsuarioModel usuarioAdm : usuariosAdm) {
				if (usuarioAdm.getTarefasCadastradas().contains(tarefa)) {
					usuarioAdm.getTarefasCadastradas().remove(tarefa);
				} else if (usuarioAdm.getTarefasAtribuidas().contains(tarefa)) {
					usuarioAdm.getTarefasAtribuidas().remove(tarefa);
				}
			}
			repository.deleteById(tarefa.getId());
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}

	}

	@ApiOperation(value = "Atualiza as propriedades de uma tarefa do sistema")
	@PutMapping("/put")
	public TarefaModel putTarefa(@RequestBody TarefaModel tarefa) {
		if (Objects.isNull(tarefa.getId()) || Objects.isNull(tarefa.getTitulo())
				|| Objects.isNull(tarefa.getUsuarioAdministrador().getId())
				|| Objects.isNull(tarefa.getUsuarioFuncionario().getId()) || Objects.isNull(tarefa.getProjeto().getId())
				|| Objects.isNull(tarefa.getDataFinal()) || Objects.isNull(tarefa.getDataInicio())) {
			return null;
		}

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

				HashMap<Integer, Integer> dict1 = new HashMap<Integer, Integer>();
				for (ProjetoModel pro : tarefaCadastrada.getUsuarioFuncionario().getProjetos()) {
					dict1.put(pro.getId(), tarefaCadastrada.getUsuarioFuncionario().getId());
				}

				if (tarefa.getDataFinal().before(tarefa.getDataInicio())) {
					return null;
				} else {
					Optional<UsuarioModel> usuarioAdministrador = repositoryUsuario
							.findById(tarefa.getUsuarioAdministrador().getId());
					UsuarioModel usuarioAdm = usuarioAdministrador.get();

					if (!usuarioAdm.getCargo().equals(EnumCargo.Administrador))
						return null;
					else
						tarefaCadastrada.setUsuarioAdministrador(usuarioAdm);

					Optional<UsuarioModel> usuarioFuncionario = repositoryUsuario
							.findById(tarefa.getUsuarioFuncionario().getId());
					UsuarioModel usuarioFunc = usuarioFuncionario.get();
					tarefaCadastrada.setUsuarioFuncionario(usuarioFunc);

					Optional<ProjetoModel> projetoOpt = repositoryProjeto.findById(tarefa.getProjeto().getId());
					ProjetoModel projeto = projetoOpt.get();
					tarefaCadastrada.setProjeto(projeto);

					if (!projeto.getFuncionarios().contains(usuarioFunc))
						projeto.setFuncionarios(usuarioFunc);

					if (!usuarioFunc.getProjetos().contains(projeto)) {
						usuarioFunc.setProjetos(projeto);
					}

					tarefaCadastrada.setDataCadastro(dataAtual);
					tarefaCadastrada.setDataFinal(formataData(tarefa.getDataFinal()));
					tarefaCadastrada.setDataInicio(formataData(tarefa.getDataInicio()));
					tarefaCadastrada.setDescricao(tarefa.getDescricao());
					if (!Objects.isNull(tarefa.getStatus()))
						tarefaCadastrada.setStatus(tarefa.getStatus());

					tarefaCadastrada.setTitulo(tarefa.getTitulo());

					if (!projeto.getTarefas().contains(tarefaCadastrada))
						projeto.setTarefas(tarefaCadastrada);

					if (!usuarioFunc.getTarefasAtribuidas().contains(tarefaCadastrada))
						usuarioFunc.setTarefasAtribuidas(tarefaCadastrada);

					if (!usuarioAdm.getTarefasCadastradas().contains(tarefaCadastrada))
						usuarioAdm.setTarefasCadastradas(tarefaCadastrada);

					HashMap<Integer, Integer> dict = new HashMap<Integer, Integer>();
					for (TarefaModel tar : repository.findAll()) {
						dict.put(tar.getProjeto().getId(), tar.getUsuarioFuncionario().getId());
					}

					for (Map.Entry<Integer, Integer> entryTarefa : dict.entrySet()) {
						if (dict1.containsValue(entryTarefa.getValue())) {
							List<Integer> listaCodigoProjeto = new ArrayList<Integer>();
							for (ProjetoModel project : usuarioFunc.getProjetos()) {
								listaCodigoProjeto.add(project.getId());
							}
							for (Integer codigoProjeto : listaCodigoProjeto) {
								if (!dict.containsKey(codigoProjeto)) {
									usuarioFunc.getProjetos().remove(repositoryProjeto.findById(codigoProjeto).get());
									projeto.getFuncionarios().remove(repositoryUsuario.findById(entryTarefa.getValue()).get());
								}
							}
						} else if(dict1.containsKey(entryTarefa.getKey())) {
							List<Integer> listaCodigoUsuario = new ArrayList<Integer>();
							for (UsuarioModel usu : projeto.getFuncionarios()) {
								listaCodigoUsuario.add(usu.getId());
							}
							for (Integer codigoUsuario : listaCodigoUsuario) {
								if (!dict.containsValue(codigoUsuario)) {
									projeto.getFuncionarios().remove(repositoryUsuario.findById(codigoUsuario).get());
								}
							}
						}
					}
					return repository.save(tarefaCadastrada);
				}
			} else
				return null;
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
