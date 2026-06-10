package br.com.gynlog.service;

import br.com.gynlog.enums.CategoriaVeiculo;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repo;

    private static class No {
        Veiculo veiculo;
        No proximo;

        No(Veiculo veiculo) {
            this.veiculo = veiculo;
            this.proximo = null;
        }
    }

    private static class FilaVeiculo {
        private No inicio;
        private No fim;
        private int tamanho;

        FilaVeiculo() {
            this.inicio = null;
            this.fim = null;
            this.tamanho = 0;
        }

        void enfileirar(Veiculo v) {
            No novo = new No(v);
            if (fim == null) {
                inicio = novo;
                fim = novo;
            } else {
                fim.proximo = novo;
                fim = novo;
            }
            tamanho++;
        }

        Veiculo desenfileirar() {
            if (inicio == null) return null;
            Veiculo v = inicio.veiculo;
            inicio = inicio.proximo;
            if (inicio == null) fim = null;
            tamanho--;
            return v;
        }

        boolean estaVazia() { return inicio == null; }
        int getTamanho()    { return tamanho; }
    }

    public void salvar(Veiculo v) throws Exception {
        validar(v);
        repo.salvar(v);
    }

    public void atualizar(Veiculo v) throws Exception {
        validar(v);
        repo.atualizar(v);
    }

    public void excluir(int id) throws Exception {
        repo.excluir(id);
    }

    public List<Veiculo> listar() throws Exception {
        return repo.buscarTodos();
    }

    public Veiculo buscarPorId(int id) throws Exception {
        return repo.buscarPorId(id);
    }

    public List<Veiculo> listarInativos() throws Exception {
        List<Veiculo> dadosBanco = repo.buscarInativos();

        FilaVeiculo fila = new FilaVeiculo();
        for (Veiculo v : dadosBanco) {
            fila.enfileirar(v);
        }

        List<Veiculo> resultado = new java.util.ArrayList<>();
        while (!fila.estaVazia()) {
            resultado.add(fila.desenfileirar());
        }

        return resultado;
    }

    public List<Veiculo> listarPorCategoria(CategoriaVeiculo categoria) throws Exception {
        return repo.buscarPorCategoria(categoria);
    }

    public List<Veiculo> buscaSequencial(String termo) throws Exception {
        List<Veiculo> todos = repo.buscarTodos();
        List<Veiculo> encontrados = new java.util.ArrayList<>();
        String termoMin = termo.toLowerCase().trim();

        for (Veiculo v : todos) {
            if (v.getPlaca().toLowerCase().contains(termoMin) || v.getModelo().toLowerCase().contains(termoMin)) {
                encontrados.add(v);
            }
        }
        return encontrados;
    }

    private void validar(Veiculo v) {
        if (v.getPlaca() == null || v.getPlaca().trim().isEmpty())
            throw new IllegalArgumentException("A placa é obrigatória.");

        if (v.getPlaca().trim().length() > 8)
            throw new IllegalArgumentException("A placa deve ter no máximo 8 caracteres.");

        if (v.getMarca() == null || v.getMarca().trim().isEmpty())
            throw new IllegalArgumentException("A marca é obrigatória.");

        if (v.getModelo() == null || v.getModelo().trim().isEmpty())
            throw new IllegalArgumentException("O modelo é obrigatório.");

        if (v.getCategoria() == null)
            throw new IllegalArgumentException("A categoria é obrigatória.");

        int anoAtual = Year.now().getValue();
        if (v.getAnoFabricacao() < 1950 || v.getAnoFabricacao() > anoAtual + 1)
            throw new IllegalArgumentException("Ano de fabricação inválido (1980 a " + (anoAtual + 1) + ").");

        try {
            for (Veiculo ve : repo.buscarTodos()) {
                if (ve.getPlaca().equalsIgnoreCase(v.getPlaca()) && ve.getIdVeiculo() != v.getIdVeiculo()) {
                    throw new IllegalArgumentException("Já existe um veículo cadastrado com esta placa.");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}