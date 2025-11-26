package br.com.gynlog.dao;

import br.com.gynlog.model.Movimentacao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovimentacaoDao {

    private static final Path ARQUIVO = Path.of("movimentacoes.txt");

    // C - Create
    public void salvar(Movimentacao m) throws IOException {
        Files.write(
                ARQUIVO,
                (m.toCsvLine() + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    // R - Read
    public List<Movimentacao> buscarTodos() throws IOException {
        if (!Files.exists(ARQUIVO)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(ARQUIVO)
                .stream()
                .map(Movimentacao::fromString)
                .collect(Collectors.toList());
    }

    // U - Update
    public void atualizar(Movimentacao mAtualizada) throws IOException {
        List<Movimentacao> lista = buscarTodos();

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdMovimentacao() == mAtualizada.getIdMovimentacao()) {
                lista.set(i, mAtualizada);
                break;
            }
        }

        reescreverArquivo(lista);
    }

    // D - Delete
    public void excluir(int idMovimentacao) throws IOException {
        List<Movimentacao> lista = buscarTodos();

        List<Movimentacao> filtrado = lista.stream()
                .filter(m -> m.getIdMovimentacao() != idMovimentacao)
                .collect(Collectors.toList());

        reescreverArquivo(filtrado);
    }

    // auxiliar
    private void reescreverArquivo(List<Movimentacao> lista) throws IOException {
        List<String> linhas = lista.stream()
                .map(Movimentacao::toCsvLine)
                .collect(Collectors.toList());

        Files.write(
                ARQUIVO,
                linhas,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}
