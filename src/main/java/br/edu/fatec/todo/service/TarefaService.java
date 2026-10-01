package br.edu.fatec.todo.service;

import br.edu.fatec.todo.dto.*;
import br.edu.fatec.todo.exception.TarefaNaoEncontradaException;
import br.edu.fatec.todo.model.Tarefa;
import br.edu.fatec.todo.repository.TarefaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TarefaService {
    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    public List<TarefaResponse> listar() {
        return repository.findAll(Sort.by("id")).stream().map(TarefaResponse::from).toList();
    }

    public TarefaResponse buscar(Long id) {
        return TarefaResponse.from(encontrar(id));
    }

    @Transactional
    public TarefaResponse criar(TarefaRequest request) {
        Tarefa tarefa = new Tarefa(request.nome(), request.descricao(), request.status(), request.observacoes());
        return TarefaResponse.from(repository.saveAndFlush(tarefa));
    }

    @Transactional
    public TarefaResponse atualizar(Long id, TarefaRequest request) {
        Tarefa tarefa = encontrar(id);
        tarefa.atualizar(request.nome(), request.descricao(), request.status(), request.observacoes());
        return TarefaResponse.from(repository.saveAndFlush(tarefa));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(encontrar(id));
    }

    private Tarefa encontrar(Long id) {
        return repository.findById(id).orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }
}