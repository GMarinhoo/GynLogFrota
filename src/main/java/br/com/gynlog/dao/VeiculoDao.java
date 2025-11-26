package br.com.gynlog.dao;

import br.com.gynlog.model.Veiculo;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VeiculoDao {

    private static final Path ARQUIVO = Paths.get("veiculos.txt");

    // Cadastrar
    public void salvar(Veiculo v) throws IOException {
        Files.write(
                ARQUIVO,
                (v.toCsvLine() + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    // Listar todos
    public List<Veiculo> buscarTodos() throws IOException {
        if (!Files.exists(ARQUIVO)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(ARQUIVO)
                .stream()
                .map(Veiculo::fromString)
                .collect(Collectors.toList());
    }

    // Atualizar veículo inteiro
    public void atualizar(Veiculo veiculoAtualizado) throws IOException {
        List<Veiculo> lista = buscarTodos();

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdVeiculo() == veiculoAtualizado.getIdVeiculo()) {
                lista.set(i, veiculoAtualizado);
                break;
            }
        }

        reescreverArquivo(lista);
    }

    // Excluir
    public void excluir(int idVeiculo) throws IOException {
        List<Veiculo> lista = buscarTodos();

        List<Veiculo> filtrado =
                lista.stream()
                        .filter(v -> v.getIdVeiculo() != idVeiculo)
                        .collect(Collectors.toList());

        reescreverArquivo(filtrado);
    }

    // Função auxiliar
    private void reescreverArquivo(List<Veiculo> lista) throws IOException {
        List<String> linhas =
                lista.stream()
                        .map(Veiculo::toCsvLine)
                        .collect(Collectors.toList());

        Files.write(ARQUIVO, linhas, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
