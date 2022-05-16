package com.project.workatus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.persistence.*;

import com.project.workatus.model.enums.EnumCargo;

import io.swagger.annotations.ApiModelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbUsuario")
@Entity
public class UsuarioModel {

	public UsuarioModel(String login, String senha, EnumCargo cargo) {
		super();
		this.login = login;
		this.senha = senha;
		this.cargo = cargo;
	}
	
	public UsuarioModel() {
		
	}

	@ApiModelProperty(value = "Id do usuário")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="USU_ID")
	private Integer id;

	@ApiModelProperty(value = "Login do usuário")
	@Column(unique = true, nullable = false, name="USU_LOGIN")
	private String login;

	@ApiModelProperty(value = "Senha do usuário")
	@Column(nullable = false, name="USU_SENHA")
	private String senha;

	@ApiModelProperty(value = "Cargo do usuário", name="USU_CARGO")
	@Column(nullable = false, name="USU_CARGO")
	private EnumCargo cargo;
	
	@OneToMany(mappedBy="usuarioAdministrador")
    private List<TarefaModel> tarefasCadastradas;
	
	@OneToMany(mappedBy="usuarioFuncionario")
    private List<TarefaModel> tarefasAtribuidas;

	public Integer getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public EnumCargo getCargo() {
		return cargo;
	}

	public void setCargo(EnumCargo cargo) {
		this.cargo = cargo;
	}

}