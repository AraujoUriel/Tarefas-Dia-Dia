package br.edu.fatec.todo.dto;

import br.edu.fatec.todo.model.StatusTarefa;
import jakarta.validation.constraints.*;

public record TarefaRequest(
    @NotBlank(message = "O nome e obrigatorio.")
    @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres.")
    String nome,
    @Size(max = 2000, message = "A descricao deve ter no maximo 2000 caracteres.")
    String descricao,
    @NotNull(message = "O status e obrigatorio.")
    StatusTarefa status,
    @Size(max = 2000, message = "As observacoes devem ter no maximo 2000 caracteres.")
    String observacoes
) {}