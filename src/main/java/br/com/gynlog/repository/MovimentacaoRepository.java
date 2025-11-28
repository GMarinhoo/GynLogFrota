package br.com.gynlog.repository;

import br.com.gynlog.model.Movimentacao;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MovimentacaoRepository {
    private static final Path ARQUIVO = Paths.get("movimentacoes.txt");

    public MovimentacaoRepository() {
        try { if (!Files.exists(ARQUIVO)) Files.createFile(ARQUIVO); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void salvar(Movimentacao m) throws IOException {
        if (m.getIdMovimentacao() == 0) m.setIdMovimentacao(gerarId());
        Files.write(ARQUIVO, (m.toCsvLine() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
    }

    public List<Movimentacao> buscarTodos() throws IOException {
        if (!Files.exists(ARQUIVO)) return new ArrayList<>();
        return Files.readAllLines(ARQUIVO).stream()
                .filter(l -> !l.trim().isEmpty())
                .map(Movimentacao::fromString)
                .collect(Collectors.toList());
    }

    public List<Movimentacao> buscarPorVeiculo(int idVeiculo) throws IOException {
        return buscarTodos().stream().filter(m -> m.getIdVeiculo() == idVeiculo).collect(Collectors.toList());
    }

    public void atualizar(Movimentacao m) throws IOException {
        List<Movimentacao> lista = buscarTodos();
        boolean achou = false;
        for(int i=0; i<lista.size(); i++) {
            if(lista.get(i).getIdMovimentacao() == m.getIdMovimentacao()) {
                lista.set(i, m);
                achou = true;
                break;
            }
        }
        if(achou) reescrever(lista);
    }

    public void excluir(int id) throws IOException {
        List<Movimentacao> lista = buscarTodos();
        lista.removeIf(m -> m.getIdMovimentacao() == id);
        reescrever(lista);
    }

    private void reescrever(List<Movimentacao> lista) throws IOException {
        List<String> linhas = lista.stream().map(Movimentacao::toCsvLine).collect(Collectors.toList());
        Files.write(ARQUIVO, linhas, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private int gerarId() throws IOException {
        return buscarTodos().stream().mapToInt(Movimentacao::getIdMovimentacao).max().orElse(0) + 1;
    }
}