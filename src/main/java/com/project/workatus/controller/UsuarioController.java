package com.project.workatus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.project.workatus.model.UsuarioModel;
import com.project.workatus.repository.UsuarioRepository;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	private final UsuarioRepository repository;
	private final PasswordEncoder encoder;

	public UsuarioController(UsuarioRepository repository, PasswordEncoder encoder) {
		this.repository = repository;
		this.encoder = encoder;
	}
	
	@ApiOperation(value = "Retorna todos os usuários cadastrados")
	@GetMapping("/getAll")
	public List<UsuarioModel> getAll() {
		return repository.findAll();
	}
	
	@ApiOperation(value = "Retorna um usuário de acordo com o Id")
	@GetMapping("/getId")
	public Optional<UsuarioModel> getUsuarioId(@RequestParam int id) {
		return repository.findById(id);
	}
	
	@ApiOperation(value = "Retorna um usuário de acordo com o Login")
	@GetMapping("/getLogin")
	public Optional<UsuarioModel> getUsuarioLogin(@RequestParam String login) {
		return repository.findByLogin(login);
	}
	
	@ApiOperation(value = "Insere um usuário no sistema")
	@PostMapping("/insert")
	public ResponseEntity<Boolean> insertUsuario(@RequestBody UsuarioModel usuario) {
		boolean loginExiste = loginValido(usuario.getLogin());

		if (loginExiste) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.save(new UsuarioModel(usuario.getLogin(), encoder.encode(usuario.getSenha()), usuario.getCargo()));
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}
	
	@ApiOperation(value = "Valida se usuário e senha existem no sistema")
	@PostMapping("/check")
	public ResponseEntity<Boolean> checkUsuario(@RequestBody UsuarioModel usuario) {
		boolean loginExiste = loginValido(usuario.getLogin());

		if (!loginExiste)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);

		Optional<UsuarioModel> usuarioExistente = repository.findByLogin(usuario.getLogin());
		UsuarioModel usuarioExiste = usuarioExistente.get();

		boolean valid = encoder.matches(usuario.getSenha(), usuarioExiste.getSenha());

		HttpStatus status = (valid) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(valid);
	}
	
	@ApiOperation(value = "Deleta um usuário de acordo com o Id")
	@DeleteMapping("/deleteId")
	public ResponseEntity<Boolean> deleteUsuarioId(@RequestParam int id) {
		Optional<UsuarioModel> optUsuario = repository.findById(id);

		if (optUsuario.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}
	
	@ApiOperation(value = "Deleta um usuário de acordo com o Login")
	@DeleteMapping("/deleteLogin")
	public ResponseEntity<Boolean> deleteUsuarioLogin(@RequestParam String login) {
		Optional<UsuarioModel> optUsuario = repository.findByLogin(login);

		if (optUsuario.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			repository.deleteById(optUsuario.get().getId());
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}

	}
	
	@ApiOperation(value = "Atualiza as propriedades de um usuário do sistema")
	@PutMapping("/put")
	public ResponseEntity<Boolean> putUsuario(@RequestBody UsuarioModel usuario) {
		boolean idExiste = idValido(usuario.getId());

		if (!idExiste) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} else {
			UsuarioModel usuarioCadastrado = repository.findById(usuario.getId()).get();

			boolean senhasIguais = encoder.matches(usuario.getSenha(), usuarioCadastrado.getSenha());
			boolean loginsIguais = usuario.getLogin().equals(usuarioCadastrado.getLogin());

			if (loginsIguais) {
				if (senhasIguais) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
				} else {
					usuarioCadastrado.setSenha(encoder.encode(usuario.getSenha()));
				}
			} else {
				Optional<UsuarioModel> usuarioComMesmoLogin = repository.findByLogin(usuario.getLogin());
				if (usuarioComMesmoLogin.isEmpty()) {
					usuarioCadastrado.setLogin(usuario.getLogin());
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
				}
				if (!senhasIguais)
					usuarioCadastrado.setSenha(encoder.encode(usuario.getSenha()));
			}
			usuarioCadastrado.setCargo(usuario.getCargo());
			repository.save(usuarioCadastrado);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		}
	}

	public boolean idValido(int id) {
		Optional<UsuarioModel> optUsuario = repository.findById(id);

		if (optUsuario.isEmpty())
			return false;
		else
			return true;
	}

	public boolean loginValido(String login) {
		Optional<UsuarioModel> optUsuario = repository.findByLogin(login);

		if (optUsuario.isEmpty())
			return false;
		else
			return true;
	}

	public boolean senhaValida(int id, String senha) {
		Optional<UsuarioModel> optUsuario = repository.findById(id);

		UsuarioModel usuarioExistente = optUsuario.get();

		return encoder.matches(senha, usuarioExistente.getSenha());
	}
}