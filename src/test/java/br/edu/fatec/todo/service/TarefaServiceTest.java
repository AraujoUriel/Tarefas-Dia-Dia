package br.edu.fatec.todo.service;

import br.edu.fatec.todo.dto.TarefaRequest;
import br.edu.fatec.todo.exception.TarefaNaoEncontradaException;
import br.edu.fatec.todo.model.*;
import br.edu.fatec.todo.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {
    @Mock TarefaRepository repository;
    @InjectMocks TarefaService service;

    @Test void deveCriarTarefaComDadosInformados() {
        when(repository.saveAndFlush(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var resultado = service.criar(new TarefaRequest("  Estudar  ", "Java", StatusTarefa.PENDENTE, "Hoje"));
        assertThat(resultado.nome()).isEqualTo("Estudar");
        assertThat(resultado.status()).isEqualTo(StatusTarefa.PENDENTE);
        verify(repository).saveAndFlush(any(Tarefa.class));
    }

    @Test void deveAtualizarTarefaExistente() {
        Tarefa tarefa = new Tarefa("Antes", null, StatusTarefa.PENDENTE, null);
        when(repository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(repository.saveAndFlush(tarefa)).thenReturn(tarefa);
        var resultado = service.atualizar(1L, new TarefaRequest("Depois", "Nova", StatusTarefa.CONCLUIDA, "Feito"));
        assertThat(resultado.nome()).isEqualTo("Depois");
        assertThat(resultado.status()).isEqualTo(StatusTarefa.CONCLUIDA);
        assertThat(resultado.observacoes()).isEqualTo("Feito");
    }

    @Test void deveExcluirTarefaExistente() {
        Tarefa tarefa = new Tarefa("Estudar", null, StatusTarefa.PENDENTE, null);
        when(repository.findById(1L)).thenReturn(Optional.of(tarefa));
        service.excluir(1L);
        verify(repository).delete(tarefa);
    }

    @Test void naoDeveExcluirTarefaInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.excluir(1L)).isInstanceOf(TarefaNaoEncontradaException.class);
        verify(repository, never()).delete(any());
    }

    @Test void naoDeveSalvarAtualizacaoDeTarefaInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atualizar(1L,
            new TarefaRequest("Nova", null, StatusTarefa.PENDENTE, null)))
            .isInstanceOf(TarefaNaoEncontradaException.class);
        verify(repository, never()).saveAndFlush(any());
    }
}