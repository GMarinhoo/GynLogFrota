package br.com.gynlog.controller;

import br.com.gynlog.model.TipoDespesa;
import br.com.gynlog.util.GerenciadorArquivos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TipoDespesaController {

    private static final String ARQUIVO_TIPOS = "tipos_despesa.txt";

    public void inicializarTiposPadrao() {
        try {
            List<String> linhas = GerenciadorArquivos.lerTodasLinhas(ARQUIVO_TIPOS);
            if (linhas.isEmpty()) {
                salvar(new TipoDespesa(1, "Combustível"));
                salvar(new TipoDespesa(2, "Manutenção"));
                salvar(new TipoDespesa(3, "IPVA"));
                salvar(new TipoDespesa(4, "Multa"));
                salvar(new TipoDespesa(5, "Seguro"));
                salvar(new TipoDespesa(6, "Limpeza"));
                salvar(new TipoDespesa(7, "Outros"));
                System.out.println("Tipos de despesa padrão criados com sucesso!");
            }
        } catch (IOException e) {
            System.err.println("Erro ao inicializar tipos: " + e.getMessage());
        }
    }

    public void salvar(TipoDespesa tipo) throws IOException {
        GerenciadorArquivos.salvarLinha(ARQUIVO_TIPOS, tipo.toCsvLine());
    }

    public List<TipoDespesa> listarTodos() throws IOException {
        List<String> linhas = GerenciadorArquivos.lerTodasLinhas(ARQUIVO_TIPOS);
        List<TipoDespesa> lista = new ArrayList<>();

        for (String linha : linhas) {
            try {
                lista.add(TipoDespesa.fromString(linha));
            } catch (Exception e) {
            }
        }
        return lista;
    }

    public String buscarDescricaoPorId(int id) throws IOException {
        for (TipoDespesa tipo : listarTodos()) {
            if (tipo.getIdTipoDespesa() == id) {
                return tipo.getDescricao();
            }
        }
        return "Desconhecido";
    }
}