package com.project.workatus.model;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import io.swagger.annotations.ApiModelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tbProjeto")
public class ProjetoModel {

    public ProjetoModel(String nome, String descricao, Date dataInicio, Date dataFinal) {
        super();
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
    }

    @ApiModelProperty(value = "Id do projeto")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "Nome do projeto")
    @Column(unique = true, nullable = false)
    private String nome;

    @ApiModelProperty(value = "Descrição do projeto")
    private String descricao;

    @ApiModelProperty(value = "Data de início do projeto")
    @Column(nullable = false)
    private Date dataInicio;

    @ApiModelProperty(value = "Data final do projeto")
    @Column(nullable = false)
    private Date dataFinal;

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }
}