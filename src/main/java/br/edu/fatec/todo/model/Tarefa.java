package br.edu.fatec.todo.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tarefas")
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(length = 2000)
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTarefa status;
    @Column(length = 2000)
    private String observacoes;
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private Instant dataCriacao;
    @Column(name = "data_atualizacao", nullable = false)
    private Instant dataAtualizacao;

    protected Tarefa() {}

    public Tarefa(String nome, String descricao, StatusTarefa status, String observacoes) {
        atualizar(nome, descricao, status, observacoes);
    }

    public void atualizar(String nome, String descricao, StatusTarefa status, String observacoes) {
        this.nome = nome.trim();
        this.descricao = descricao;
        this.status = status;
        this.observacoes = observacoes;
    }

    @PrePersist
    void antesDeCriar() {
        dataCriacao = Instant.now().truncatedTo(ChronoUnit.MICROS);
        dataAtualizacao = dataCriacao;
    }

    @PreUpdate
    void antesDeAtualizar() {
        dataAtualizacao = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public StatusTarefa getStatus() { return status; }
    public String getObservacoes() { return observacoes; }
    public Instant getDataCriacao() { return dataCriacao; }
    public Instant getDataAtualizacao() { return dataAtualizacao; }
}