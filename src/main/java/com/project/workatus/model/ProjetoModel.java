package com.project.workatus.model;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import io.swagger.annotations.ApiModelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbProjeto")
@Entity
public class ProjetoModel {

    public ProjetoModel(String nome, String descricao, Date dataInicio, Date dataFinal) {
        super();
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
    }
    
    public ProjetoModel() {
    	
    }

    @ApiModelProperty(value = "Id do projeto")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PRO_ID")
    private Integer id;

    @ApiModelProperty(value = "Nome do projeto")
    @Column(unique = true, nullable = false, name="PRO_NOME")
    private String nome;

    @ApiModelProperty(value = "Descrição do projeto")
    @Column(name="PRO_DESCRICAO")
    private String descricao;

    @ApiModelProperty(value = "Data de início do projeto")
    @Column(nullable = false, name="PRO_DATA_INICIO")
    private Date dataInicio;

    @ApiModelProperty(value = "Data final do projeto")
    @Column(nullable = false, name="PRO_DATA_FINAL")
    private Date dataFinal;
    
    @OneToMany(mappedBy="projeto")
    private List<TarefaModel> tarefas;

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