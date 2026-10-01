package br.edu.fatec.todo;

import br.edu.fatec.todo.repository.TarefaRepository;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TarefaIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired TarefaRepository repository;

    @BeforeEach void limpar() { repository.deleteAll(); }

    private String corpo(String nome, String status) throws Exception {
        return mapper.writeValueAsString(Map.of("nome", nome, "descricao", "Estudar Spring",
            "status", status, "observacoes", "Entregar no prazo"));
    }

    @Test
    void deveExecutarCicloCompletoDoCrud() throws Exception {
        mvc.perform(get("/api/tarefas")).andExpect(status().isOk()).andExpect(content().json("[]"));
        String resposta = mvc.perform(post("/api/tarefas").contentType("application/json")
            .content(corpo("  Trabalho  ", "PENDENTE")))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.nome").value("Trabalho"))
            .andExpect(jsonPath("$.descricao").value("Estudar Spring"))
            .andExpect(jsonPath("$.observacoes").value("Entregar no prazo"))
            .andExpect(jsonPath("$.dataCriacao").isNotEmpty())
            .andReturn().getResponse().getContentAsString();
        JsonNode criada = mapper.readTree(resposta);
        long id = criada.get("id").asLong();
        mvc.perform(get("/api/tarefas/{id}", id)).andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDENTE"));
        mvc.perform(get("/api/tarefas")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
        String atualizada = mvc.perform(put("/api/tarefas/{id}", id).contentType("application/json")
            .content(corpo("Finalizar trabalho", "CONCLUIDA")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CONCLUIDA"))
            .andExpect(jsonPath("$.nome").value("Finalizar trabalho"))
            .andReturn().getResponse().getContentAsString();
        JsonNode alterada = mapper.readTree(atualizada);
        assertThat(Instant.parse(alterada.get("dataCriacao").asText()))
            .isEqualTo(Instant.parse(criada.get("dataCriacao").asText()));
        assertThat(Instant.parse(alterada.get("dataAtualizacao").asText()))
            .isAfter(Instant.parse(criada.get("dataAtualizacao").asText()));
        mvc.perform(delete("/api/tarefas/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/tarefas/{id}", id)).andExpect(status().isNotFound());
        assertThat(repository.count()).isZero();
    }

    @Test
    void deveRejeitarNomeVazioEStatusAusente() throws Exception {
        mvc.perform(post("/api/tarefas").contentType("application/json").content("{\"nome\":\"   \"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.erros.nome").exists())
            .andExpect(jsonPath("$.erros.status").exists());
        assertThat(repository.count()).isZero();
    }

    @Test
    void deveRejeitarStatusInvalidoEJsonMalformado() throws Exception {
        mvc.perform(post("/api/tarefas").contentType("application/json").content(corpo("Tarefa", "ERRADO")))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/tarefas").contentType("application/json").content("{"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarCamposAcimaDoLimite() throws Exception {
        String payload = mapper.writeValueAsString(Map.of("nome", "a".repeat(121),
            "descricao", "a".repeat(2001), "observacoes", "a".repeat(2001), "status", "PENDENTE"));
        mvc.perform(post("/api/tarefas").contentType("application/json").content(payload))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.erros.nome").exists())
            .andExpect(jsonPath("$.erros.descricao").exists()).andExpect(jsonPath("$.erros.observacoes").exists());
    }

    @Test
    void deveRetornar404ParaOperacoesComIdInexistente() throws Exception {
        mvc.perform(get("/api/tarefas/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/tarefas/999999").contentType("application/json")
            .content(corpo("Tarefa", "PENDENTE"))).andExpect(status().isNotFound());
        mvc.perform(delete("/api/tarefas/999999")).andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400ParaIdInvalido() throws Exception {
        mvc.perform(get("/api/tarefas/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void atualizacaoInvalidaNaoDeveAlterarTarefa() throws Exception {
        String resposta = mvc.perform(post("/api/tarefas").contentType("application/json")
            .content(corpo("Original", "PENDENTE"))).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(resposta).get("id").asLong();
        mvc.perform(put("/api/tarefas/{id}", id).contentType("application/json")
            .content(corpo(" ", "CONCLUIDA"))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/tarefas/{id}", id)).andExpect(jsonPath("$.nome").value("Original"))
            .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void devePermitirCamposOpcionaisAusentes() throws Exception {
        mvc.perform(post("/api/tarefas").contentType("application/json")
            .content("{\"nome\":\"Minima\",\"status\":\"EM_ANDAMENTO\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }
}