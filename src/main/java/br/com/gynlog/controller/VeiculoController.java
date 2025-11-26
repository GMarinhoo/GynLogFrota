package br.com.gynlog.controller;

import br.com.gynlog.model.Veiculo;
import br.com.gynlog.util.GerenciadorArquivos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VeiculoController {

    private static final String ARQUIVO_VEICULOS = "veiculos.txt";

    public List<Veiculo> listarTodos() throws IOException {
        List<String> linhas = GerenciadorArquivos.lerTodasLinhas(ARQUIVO_VEICULOS);
        List<Veiculo> listaVeiculos = new ArrayList<>();

        for (String linha : linhas) {
            try {
                Veiculo v = Veiculo.fromString(linha);
                listaVeiculos.add(v);
            } catch (Exception e) {
                System.err.println("Linha ignorada: " + e.getMessage());
            }
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

    public void salvar(Veiculo veiculo) throws IOException {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().isEmpty()) {
            throw new IllegalArgumentException("A placa do veículo é obrigatória!");
        }

        if (veiculo.getIdVeiculo() == 0) {
            int novoId = GerenciadorArquivos.gerarProximoId(ARQUIVO_VEICULOS);
            veiculo.setIdVeiculo(novoId);
            GerenciadorArquivos.salvarLinha(ARQUIVO_VEICULOS, veiculo.toCsvLine());
        } else {
            editar(veiculo);
        }
    }

    private void editar(Veiculo veiculoEditado) throws IOException {
        List<Veiculo> listaAtual = listarTodos();
        List<String> novasLinhas = new ArrayList<>();
        boolean encontrou = false;

        for (Veiculo v : listaAtual) {
            if (v.getIdVeiculo() == veiculoEditado.getIdVeiculo()) {
                novasLinhas.add(veiculoEditado.toCsvLine());
                encontrou = true;
            } else {
                novasLinhas.add(v.toCsvLine());
            }
        }

        if (encontrou) {
            GerenciadorArquivos.reescreverArquivo(ARQUIVO_VEICULOS, novasLinhas);
        } else {
            throw new IllegalArgumentException("Veículo para edição não encontrado (ID: " + veiculoEditado.getIdVeiculo() + ")");
        }
    }

    public void excluir(int idVeiculo) throws IOException {
        List<Veiculo> listaAtual = listarTodos();
        List<String> novasLinhas = new ArrayList<>();
        boolean encontrou = false;

        for (Veiculo v : listaAtual) {
            if (v.getIdVeiculo() == idVeiculo) {
                encontrou = true;
            } else {
                novasLinhas.add(v.toCsvLine());
            }
        }

        if (encontrou) {
            GerenciadorArquivos.reescreverArquivo(ARQUIVO_VEICULOS, novasLinhas);
        } else {
            throw new IllegalArgumentException("Veículo não encontrado para exclusão.");
        }
    }
}