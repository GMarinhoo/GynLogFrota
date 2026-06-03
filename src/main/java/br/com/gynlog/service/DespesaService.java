package br.com.gynlog.service;

import br.com.gynlog.model.Despesa;
import br.com.gynlog.repository.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class DespesaService {

    @Autowired
    private DespesaRepository repo;

    public void salvar(Despesa d) throws Exception {
        validar(d);
        repo.salvar(d);
    }

    public void atualizar(Despesa d) throws Exception {
        if (d.isGeradaPorAbastecimento())
            throw new IllegalArgumentException("Esta despesa foi gerada automaticamente por um abastecimento e não pode ser editada manualmente.");
        validar(d);
        repo.atualizar(d);
    }

    public void excluir(int id) throws Exception {
        Despesa existente = repo.buscarPorId(id);
        if (existente != null && existente.isGeradaPorAbastecimento())
            throw new IllegalArgumentException("Esta despesa foi gerada automaticamente por um abastecimento. Exclua o abastecimento correspondente.");
        repo.excluir(id);
    }

    public List<Despesa> listar() throws Exception {
        return repo.buscarTodos();
    }

    public Despesa buscarPorId(int id) throws Exception {
        return repo.buscarPorId(id);
    }

    public List<Despesa> listarPorVeiculo(int idVeiculo) throws Exception {
        List<Despesa> lista = repo.buscarPorVeiculo(idVeiculo);
        ordenarPorValor(lista);
        return lista;
    }

    public List<Despesa> listarPorMes(int mes, int ano) throws Exception {
        validarMesAno(mes, ano);
        return repo.buscarPorMes(mes, ano);
    }

    public List<Despesa> listarCombustivelPorMes(int mes, int ano) throws Exception {
        validarMesAno(mes, ano);
        return repo.buscarCombustivelPorMes(mes, ano);
    }

    public List<Despesa> listarIpvaPorAno(int ano) throws Exception {
        validarAno(ano);
        return repo.buscarIpvaPorAno(ano);
    }

    public double somarIpvaRecursivo(List<Despesa> lista, int indice) {
        if (indice >= lista.size()) return 0.0;

        return lista.get(indice).getValor() + somarIpvaRecursivo(lista, indice + 1);
    }

    public List<Despesa> listarMultasPorVeiculoAno(int idVeiculo, int ano) throws Exception {
        validarAno(ano);
        return repo.buscarMultasPorVeiculoAno(idVeiculo, ano);
    }

    public List<Object[]> listarMediaGastoPorCategoria() throws Exception {
        return repo.buscarMediaGastoPorCategoria();
    }

    public List<Object[]> listarMediaIpvaPorAno(int ano) throws Exception {
        validarAno(ano);
        return repo.buscarMediaIpvaPorAno(ano);
    }

    private void ordenarPorValor(List<Despesa> lista) {
        int n = lista.size();
        boolean houveTroca;

        for (int i = 0; i < n - 1; i++) {
            houveTroca = false;

            for (int j = 0; j < n - 1 - i; j++) {
                if (lista.get(j).getValor() > lista.get(j + 1).getValor()) {
                    Despesa temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                    houveTroca = true;
                }
            }
            if (!houveTroca) break;
        }
    }

    private void validar(Despesa d) {
        if (d.getTipoDespesa() == null)
            throw new IllegalArgumentException("O tipo de despesa é obrigatório.");

        if (d.getDescricao() == null || d.getDescricao().trim().isEmpty())
            throw new IllegalArgumentException("A descrição é obrigatória.");

        if (d.getDescricao().trim().length() > 100)
            throw new IllegalArgumentException("A descrição deve ter no máximo 100 caracteres.");

        if (d.getValor() <= 0)
            throw new IllegalArgumentException("O valor deve ser maior que zero.");

        if (d.getData() == null)
            throw new IllegalArgumentException("A data é obrigatória.");

        if (d.getData().getYear() < 2000 || d.getData().getYear() > java.time.Year.now().getValue() + 1)
            throw new IllegalArgumentException("Data inválida.");
    }

    private void validarMesAno(int mes, int ano) {
        if (mes < 1 || mes > 12)
            throw new IllegalArgumentException("Mês inválido (1 a 12).");
        validarAno(ano);
    }

    private void validarAno(int ano) {
        if (ano < 2000 || ano > java.time.Year.now().getValue() + 1)
            throw new IllegalArgumentException("Ano inválido.");
    }
}