package br.com.gynlog.repository;

import br.com.gynlog.enums.TipoUsuario;
import br.com.gynlog.model.Usuario;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UsuarioRepository {
    private static final Path ARQUIVO = Paths.get("usuarios.txt");

    public UsuarioRepository() {
        try {
            if (!Files.exists(ARQUIVO)) {
                Files.createFile(ARQUIVO);
                Usuario admin = new Usuario(1, "admin", "admin", "Administrador", TipoUsuario.GERENTE);
                Files.write(ARQUIVO, (admin.toCsvLine() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public Usuario buscarPorLoginSenha(String login, String senha) throws IOException {
        for (Usuario u : buscarTodos()) {
            if (u.getLogin().equals(login) && u.getSenha().equals(senha)) return u;
        }
        return null;
    }

    public List<Usuario> buscarTodos() throws IOException {
        if (!Files.exists(ARQUIVO)) return new ArrayList<>();
        return Files.readAllLines(ARQUIVO).stream()
                .filter(l -> !l.trim().isEmpty())
                .map(Usuario::fromString)
                .collect(Collectors.toList());
    }

    public void salvar(Usuario u) throws IOException {
        if (u.getId() == 0) u.setId(gerarId());
        Files.write(ARQUIVO, (u.toCsvLine() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
    }

    public void atualizar(Usuario u) throws IOException {
        List<Usuario> lista = buscarTodos();
        for(int i=0; i<lista.size(); i++) {
            if(lista.get(i).getId() == u.getId()) {
                lista.set(i, u);
                break;
            }
        }
        reescrever(lista);
    }

    public void excluir(int id) throws IOException {
        List<Usuario> lista = buscarTodos();
        lista.removeIf(u -> u.getId() == id);
        reescrever(lista);
    }

    private void reescrever(List<Usuario> lista) throws IOException {
        List<String> linhas = lista.stream().map(Usuario::toCsvLine).collect(Collectors.toList());
        Files.write(ARQUIVO, linhas, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private int gerarId() throws IOException {
        return buscarTodos().stream().mapToInt(Usuario::getId).max().orElse(0) + 1;
    }
}