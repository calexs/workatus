package com.project.workatus.model;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import com.project.workatus.model.enums.EnumStatus;
import io.swagger.annotations.ApiModelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbTarefa")
@Entity
public class TarefaModel {

    public TarefaModel(String comentario, String descricao, EnumStatus status, Date dataCadastro, Date dataInicio,
            Date dataFinal, UsuarioModel usuarioAdministrador, UsuarioModel usuarioFuncionario, ProjetoModel projeto) {
        super();
        this.titulo = comentario;
        this.descricao = descricao;
        this.status = status;
        this.dataCadastro = dataCadastro;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
        this.usuarioAdministrador = usuarioAdministrador;
        this.usuarioFuncionario = usuarioFuncionario;
        this.projeto = projeto;
    }
    
    public TarefaModel() {
    	
    }

    @ApiModelProperty(value = "Id da tarefa")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, name= "TAR_ID")
    private Integer id;

    @ApiModelProperty(value = "Título da tarefa")
    @Column(unique = true, name= "TAR_TITULO")
    private String titulo;

    @ApiModelProperty(value = "Descrição da tarefa")
    @Column(name="TAR_DESCRICAO")
    private String descricao;

    @ApiModelProperty(value = "Status da tarefa")
    @Column(nullable = false, name="TAR_STATUS")
    private EnumStatus status;

    @ApiModelProperty(value = "Data de cadastro da tarefa")
    @Column(nullable = false, name="TAR_DATA_CADASTRO")
    private Date dataCadastro;

    @ApiModelProperty(value = "Data de início da tarefa")
    @Column(name="TAR_DATA_INICIO")
    private Date dataInicio;

    @ApiModelProperty(value = "Data de término da tarefa")
    @Column(name="TAR_DATA_FINAL")
    private Date dataFinal;

    @ApiModelProperty(value = "Usuário administrador que cadastrou a tarefa")
    @ManyToOne
    @JoinColumn(name="USU_ID_ADMIN")
    private UsuarioModel usuarioAdministrador;

    @ApiModelProperty(value = "Usuário funcionário que irá executar a tarefa")
    @ManyToOne
    @JoinColumn(name="USU_ID_FUNC")
    private UsuarioModel usuarioFuncionario;

    @ApiModelProperty(value = "Projeto a qual a tarefa pertence")
    @ManyToOne
    @JoinColumn(name="PRO_ID")
    private ProjetoModel projeto;
    
    @OneToMany(mappedBy="tarefa")
    private List<PostagemModel> postagens;

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EnumStatus getStatus() {
        return status;
    }

    public void setStatus(EnumStatus status) {
        this.status = status;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
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

    public UsuarioModel getUsuarioAdministrador() {
        return usuarioAdministrador;
    }

    public void setUsuarioAdministrador(UsuarioModel usuarioAdministrador) {
        this.usuarioAdministrador = usuarioAdministrador;
    }

    public UsuarioModel getUsuarioFuncionario() {
        return usuarioFuncionario;
    }

    public void setUsuarioFuncionario(UsuarioModel usuarioFuncionario) {
        this.usuarioFuncionario = usuarioFuncionario;
    }

    public ProjetoModel getProjeto() {
        return projeto;
    }

    public void setProjeto(ProjetoModel projeto) {
        this.projeto = projeto;
    }
}
