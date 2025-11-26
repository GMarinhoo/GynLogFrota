package br.com.gynlog.controller;

import br.com.gynlog.model.Veiculo;
import br.com.gynlog.util.GerenciadorArquivos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VeiculoController {

    private static final String ARQUIVO_VEICULOS = "veiculos.txt";

    public void salvar(Veiculo veiculo) throws IOException {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().isEmpty()) {
            throw new IllegalArgumentException("A placa do veículo é obrigatória!");
        }

        GerenciadorArquivos.salvarLinha(ARQUIVO_VEICULOS, veiculo.toCsvLine());
    }

    public List<Veiculo> listarTodos() throws IOException {
        List<String> linhas = GerenciadorArquivos.lerTodasLinhas(ARQUIVO_VEICULOS);
        List<Veiculo> listaVeiculos = new ArrayList<>();

        for (String linha : linhas) {
            Veiculo v = Veiculo.fromString(linha);
            listaVeiculos.add(v);
        }

        return listaVeiculos;
    }

    public List<Veiculo> listarAtivos() throws IOException {
        List<Veiculo> todos = listarTodos();
        List<Veiculo> ativos = new ArrayList<>();

        for (Veiculo v : todos) {
            if (v.isAtivo()) {
                ativos.add(v);
            }
        }
        return ativos;
    }
}