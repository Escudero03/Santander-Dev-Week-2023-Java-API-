package com.santander.devweek.controller;

import com.santander.devweek.model.Usuario;
import com.santander.devweek.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/hello")
    public String helloWorld() {
        return "Olá, Santander Dev Week 2023! API rodando com sucesso! 🚀";
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            logger.info("Criando novo usuário: {}", usuario.getNome());
            
            // Validações
            Objects.requireNonNull(usuario.getNome(), "Nome não pode ser nulo");
            Objects.requireNonNull(usuario.getEmail(), "Email não pode ser nulo");
            Objects.requireNonNull(usuario.getSenha(), "Senha não pode ser nula");

            Usuario novoUsuario = usuarioService.criarUsuario(usuario);
            logger.info("Usuário criado com sucesso: {}", novoUsuario.getNome());

            return ResponseEntity.created(null).body(novoUsuario);
        } catch (Exception e) {
            logger.error("Erro ao criar usuário", e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {
        try {
            logger.info("Atualizando usuário: ID {}", id);
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário", e);
            throw e;
        }
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        Map<String, Object> estatisticas = new HashMap<>();

        estatisticas.put("total_usuarios", (long) usuarios.size());
        estatisticas.put("primeiro_usuario", 
            usuarios.isEmpty() ? "Sem usuários" : usuarios.get(0).getNome());
        estatisticas.put("ultimo_usuario", 
            usuarios.isEmpty() ? "Sem usuários" : usuarios.get(usuarios.size() - 1).getNome());

        String usuarioMaisLongo = usuarios.stream()
            .max(Comparator.comparingInt(u -> u.getNome().length()))
            .map(Usuario::getNome)
            .orElse("Nenhum usuário");
        estatisticas.put("usuario_nome_mais_longo", usuarioMaisLongo);

        Map<String, Long> distribuicaoEmails = usuarios.stream()
            .map(u -> u.getEmail().split("@")[1])
            .collect(Collectors.groupingBy(
                Function.identity(), 
                Collectors.counting()
            ));
        estatisticas.put("distribuicao_emails", distribuicaoEmails);

        // Estatísticas adicionais
        OptionalDouble mediaCaracteresNome = usuarios.stream()
            .mapToInt(u -> u.getNome().length())
            .average();
        estatisticas.put("media_caracteres_nome", 
            mediaCaracteresNome.isPresent() ? mediaCaracteresNome.getAsDouble() : 0);

        logger.info("Estatísticas geradas: {}", estatisticas);
        return ResponseEntity.ok(estatisticas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}