package br.com.gynlog.service;

import br.com.gynlog.model.Usuario;
import br.com.gynlog.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repo;

    public Usuario autenticar(String login, String senha) {
        try {
            return repo.buscarPorLoginSenha(login, senha);
        } catch (IOException e) { return null; }
    }

    public void salvar(Usuario u) throws IOException {
        if(u.getLogin().isEmpty() || u.getSenha().isEmpty()) throw new IllegalArgumentException("Dados incompletos");
        repo.salvar(u);
    }

    public void atualizar(Usuario u) throws IOException {
        repo.atualizar(u);
    }

    public void excluir(int id) throws IOException {
        repo.excluir(id);
    }

    public List<Usuario> listar() throws IOException {
        return repo.buscarTodos();
    }

    public Usuario buscarPorId(int id) throws IOException {
        return listar().stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }
}