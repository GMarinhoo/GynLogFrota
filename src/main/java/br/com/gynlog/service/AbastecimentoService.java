package br.com.gynlog.service;

import br.com.gynlog.model.Abastecimento;
import br.com.gynlog.model.Despesa;
import br.com.gynlog.model.TipoDespesa;
import br.com.gynlog.repository.AbastecimentoRepository;
import br.com.gynlog.repository.DespesaRepository;
import br.com.gynlog.repository.TipoDespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.List;

@Service
public class AbastecimentoService {

    @Autowired
    private AbastecimentoRepository repo;

    @Autowired
    private DespesaRepository despesaRepo;

    @Autowired
    private TipoDespesaRepository tipoDespesaRepo;

    public void salvar(Abastecimento a) throws Exception {
        validar(a);
        repo.salvar(a);
        gerarDespesaAutomatica(a);
    }

    public void atualizar(Abastecimento a) throws Exception {
        validar(a);
        repo.atualizar(a);
    }

    public void excluir(int id) throws Exception {
        repo.excluir(id);
    }

    public List<Abastecimento> listar() throws Exception {
        return repo.buscarTodos();
    }

    public Abastecimento buscarPorId(int id) throws Exception {
        return repo.buscarPorId(id);
    }

    public List<Abastecimento> listarPorVeiculo(int idVeiculo) throws Exception {
        return repo.buscarPorVeiculo(idVeiculo);
    }

    public List<Object[]> listarConsumoMedioPorVeiculo() throws Exception {
        return repo.buscarConsumoMedioPorVeiculo();
    }

    public List<Abastecimento> listarPorVeiculoComConsumo(int idVeiculo) throws Exception {
        List<Abastecimento> lista = repo.buscarPorVeiculo(idVeiculo);

        for (int i = 0; i < lista.size(); i++) {
            Abastecimento atual = lista.get(i);

            if (i == 0) {
                atual.setKmRodados(0);
                atual.setKmPorLitro(0);
            } else {
                Abastecimento anterior = lista.get(i - 1);
                double kmRodados = atual.getOdometro() - anterior.getOdometro();
                atual.setKmRodados(kmRodados);

                if (atual.getQtdLitros() > 0)
                    atual.setKmPorLitro(kmRodados / atual.getQtdLitros());
            }
        }

        return lista;
    }

    private void gerarDespesaAutomatica(Abastecimento a) throws Exception {
        TipoDespesa tipoCombustivel = tipoDespesaRepo.buscarPorId(TipoDespesa.ID_COMBUSTIVEL);

        String descricao = String.format("Abastecimento em %s - %.2f L",
                a.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                a.getQtdLitros());

        Despesa despesa = new Despesa(
                0,
                a.getIdVeiculo(),
                tipoCombustivel,
                descricao,
                a.getData(),
                a.getValorTotal(),
                true
        );

        despesaRepo.salvar(despesa);
    }

    private void validar(Abastecimento a) throws Exception {
        if (a.getIdVeiculo() <= 0)
            throw new IllegalArgumentException("Selecione um veículo.");

        if (a.getData() == null)
            throw new IllegalArgumentException("A data é obrigatória.");

        if (a.getOdometro() <= 0)
            throw new IllegalArgumentException("O hodômetro deve ser maior que zero.");

        if (a.getQtdLitros() <= 0)
            throw new IllegalArgumentException("A quantidade de litros deve ser maior que zero.");

        if (a.getValorTotal() <= 0)
            throw new IllegalArgumentException("O valor total deve ser maior que zero.");

        Abastecimento anterior = repo.buscarAnterior(a.getIdVeiculo(), a.getOdometro());
        if (anterior != null && a.getOdometro() <= anterior.getOdometro())
            throw new IllegalArgumentException(
                String.format("O hodômetro deve ser maior que o último registrado (%.0f km).", anterior.getOdometro())
            );
    }
}
