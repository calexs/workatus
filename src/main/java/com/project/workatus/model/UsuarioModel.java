package com.project.workatus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.project.workatus.model.enums.EnumCargo;

import io.swagger.annotations.ApiModelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tbUsuario")
public class UsuarioModel {

	public UsuarioModel(String login, String senha, EnumCargo cargo) {
		super();
		this.login = login;
		this.senha = senha;
		this.cargo = cargo;
	}
	
	@ApiModelProperty(value = "Id do usuário")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ApiModelProperty(value = "Login do usuário")
	@Column(unique = true)
	private String login;
	
	@ApiModelProperty(value = "Senha do usuário")
	private String senha;
	
	@ApiModelProperty(value = "Cargo do usuário")
	private EnumCargo cargo;

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