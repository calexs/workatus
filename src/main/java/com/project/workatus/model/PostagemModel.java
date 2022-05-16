package com.project.workatus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbPostagem")
@Entity
public class PostagemModel {

    public PostagemModel(String comentario, TarefaModel tarefa) {
        this.comentario = comentario;
        this.tarefa = tarefa;
    }
    
    public PostagemModel() {
    	
    }

    @ApiModelProperty(value = "Id da postagem")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="POS_ID")
    private Integer id;

    @ApiModelProperty(value = "Comentário da postagem")
    @Column(name="POS_COMENTARIO")
    private String comentario;

    @ApiModelProperty(value = "Tarefa da postagem")
    @ManyToOne
    @JoinColumn(name="TAR_ID")
    private TarefaModel tarefa;

    public Integer getId() {
        return id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public TarefaModel getTarefa() {
        return tarefa;
    }

    public void setTarefa(TarefaModel tarefa) {
        this.tarefa = tarefa;
    }

}
