package br.com.gynlog.service;

import br.com.gynlog.dao.MovimentacaoDao;
import br.com.gynlog.dao.VeiculoDao;

import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.enums.TipoDespesaEnum;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class MovimentacaoService {

    private final MovimentacaoDao movDao = new MovimentacaoDao();
    private final VeiculoDao vecDao = new VeiculoDao();

    public void salvar(Movimentacao m) throws IOException {
        validarMovimentacao(m);
        movDao.salvar(m);
    }

    private void validarMovimentacao(Movimentacao m) throws IOException {
        if (m.getValor()<=0)
            throw new IllegalArgumentException("Valor da movimentação invalido.");

        if (m.getDescricao()== null || m.getDescricao().isBlank())
            throw new IllegalArgumentException("Descrição inválida.");

        if(m.getData()== null || m.getData().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Data inválida.");

        Veiculo v = vecDao.buscarTodos()
                .stream()
                .filter(veiculo -> veiculo.getIdVeiculo() == m.getIdVeiculo())
                .findFirst()
                .orElse(null);

        if (v==null)
            throw new IllegalArgumentException("Veículo não cadastrado.");

        if (!v.isAtivo())
            throw new IllegalArgumentException("Veículo inativo.");
        }

    public List<Movimentacao> despesasPorVeiculo(int idVeiculo) throws IOException {
        return movDao.buscarTodos()
                .stream()
                .filter(m -> m.getIdVeiculo() == idVeiculo)
                .collect(Collectors.toList());
    }
    public double totalGeralPorMes(int mes, int ano) throws IOException {
        return movDao.buscarTodos()
                .stream()
                .filter(m -> m.getData().getMonthValue() == mes)
                .filter(m -> m.getData().getYear() == ano)
                .mapToDouble(m -> m.getValor())
                .sum();
    }
    public double totalCombustivelPorMes(int mes, int ano) throws IOException {
        return movDao.buscarTodos()
                .stream()
                .filter(m -> m.getTipoDespesa() == TipoDespesaEnum.COMBUSTIVEL)
                .filter(m -> m.getData().getMonthValue() == mes)
                .filter(m -> m.getData().getYear() == ano)
                .mapToDouble(m -> m.getValor())
                .sum();
    }
    public double totalIpvaAno(int ano) throws IOException {
        return movDao.buscarTodos()
                .stream()
                .filter(m -> m.getTipoDespesa() == TipoDespesaEnum.IPVA)
                .filter(m -> m.getData().getYear() == ano)
                .mapToDouble(m -> m.getValor())
                .sum();
    }
    public double multasPorVeiculoAno(int idVeiculo, int ano) throws IOException {
        return movDao.buscarTodos()
                .stream()
                .filter(m -> m.getIdVeiculo() == idVeiculo)
                .filter(m -> m.getTipoDespesa() == TipoDespesaEnum.MULTA)
                .filter(m -> m.getData().getYear() == ano)
                .mapToDouble(m -> m.getValor())
                .sum();
    }






}
