package com.santander.devweek.controller;

import com.santander.devweek.model.Usuario;
import com.santander.devweek.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint de teste
    @GetMapping("/hello")
    public String helloWorld() {
        return "Olá, Santander Dev Week 2023! API rodando com sucesso! 🚀";
    }

    // Endpoint para criar um novo usuário
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            System.out.println("DEBUG: Recebendo solicitação de criação de usuário");
            System.out.println("DEBUG: Nome recebido: " + usuario.getNome());
            System.out.println("DEBUG: Email recebido: " + usuario.getEmail());
            System.out.println("DEBUG: Senha recebida: " + usuario.getSenha());
            
            // Verificações adicionais
            if (usuario.getNome() == null) {
                throw new IllegalArgumentException("Nome não pode ser nulo");
            }
            if (usuario.getEmail() == null) {
                throw new IllegalArgumentException("Email não pode ser nulo");
            }
            if (usuario.getSenha() == null) {
                throw new IllegalArgumentException("Senha não pode ser nula");
            }
            
            Usuario novoUsuario = usuarioService.criarUsuario(usuario);
            System.out.println("DEBUG: Usuário criado com sucesso");
            
            return ResponseEntity.created(null).body(novoUsuario);
        } catch (Exception e) {
            System.err.println("ERRO DETALHADO ao criar usuário:");
            e.printStackTrace();
            throw e;
        }
    }

    // Endpoint para listar todos os usuários
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Endpoint para buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id) {
        return usuarioService.buscarUsuarioPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(
        @PathVariable Long id, 
        @Valid @RequestBody Usuario usuario
    ) {
        try {
            // Log de depuração
            System.out.println("DEBUG: Tentando atualizar usuário");
            System.out.println("ID: " + id);
            System.out.println("Nome recebido: " + usuario.getNome());
            System.out.println("Email recebido: " + usuario.getEmail());
            
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar usuário:");
            e.printStackTrace();
            throw e;
        }
    }

    // Endpoint para deletar usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}