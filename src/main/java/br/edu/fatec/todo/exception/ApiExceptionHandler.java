package br.edu.fatec.todo.exception;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ProblemDetail naoEncontrada(TarefaNaoEncontradaException exception) {
        return problema(HttpStatus.NOT_FOUND, "Tarefa nao encontrada", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail dadosInvalidos(MethodArgumentNotValidException exception) {
        ProblemDetail problem = problema(HttpStatus.BAD_REQUEST, "Dados invalidos", "Confira os campos informados.");
        Map<String, String> erros = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(erro ->
            erros.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        problem.setProperty("erros", erros);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail jsonInvalido() {
        return problema(HttpStatus.BAD_REQUEST, "JSON invalido",
            "Envie um objeto JSON valido. Status permitidos: PENDENTE, EM_ANDAMENTO, CONCLUIDA.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail parametroInvalido() {
        return problema(HttpStatus.BAD_REQUEST, "Parametro invalido", "O id deve ser um numero inteiro valido.");
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detalhe);
        problem.setTitle(titulo);
        return problem;
    }
}