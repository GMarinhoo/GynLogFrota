package br.com.gynlog.repository;

import br.com.gynlog.model.Veiculo;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class VeiculoRepository {
    private static final Path ARQUIVO = Paths.get("veiculos.txt");

    public VeiculoRepository() {
        try { if (!Files.exists(ARQUIVO)) Files.createFile(ARQUIVO); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void salvar(Veiculo v) throws IOException {
        if (v.getIdVeiculo() == 0) v.setIdVeiculo(gerarId());
        Files.write(ARQUIVO, (v.toCsvLine() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
    }

    public List<Veiculo> buscarTodos() throws IOException {
        if (!Files.exists(ARQUIVO)) return new ArrayList<>();
        return Files.readAllLines(ARQUIVO).stream()
                .filter(l -> !l.trim().isEmpty())
                .map(Veiculo::fromString)
                .collect(Collectors.toList());
    }

    public void atualizar(Veiculo v) throws IOException {
        List<Veiculo> lista = buscarTodos();
        boolean achou = false;
        for(int i=0; i<lista.size(); i++) {
            if(lista.get(i).getIdVeiculo() == v.getIdVeiculo()) {
                lista.set(i, v);
                achou = true;
                break;
            }
        }
        if(achou) reescrever(lista);
    }

    public void excluir(int id) throws IOException {
        List<Veiculo> lista = buscarTodos();
        lista.removeIf(v -> v.getIdVeiculo() == id);
        reescrever(lista);
    }

    private void reescrever(List<Veiculo> lista) throws IOException {
        List<String> linhas = lista.stream().map(Veiculo::toCsvLine).collect(Collectors.toList());
        Files.write(ARQUIVO, linhas, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private int gerarId() throws IOException {
        return buscarTodos().stream().mapToInt(Veiculo::getIdVeiculo).max().orElse(0) + 1;
    }
}