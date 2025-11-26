package br.com.gynlog.service;

import br.com.gynlog.dao.VeiculoDao;
import br.com.gynlog.model.Veiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoDao dao;

    public void cadastrar(Veiculo v) throws Exception {

        if (v.getPlaca() == null || v.getPlaca().trim().isEmpty()) {
            throw new Exception("A placa é obrigatória!");
        }

        dao.salvar(v);
        System.out.println("Veículo salvo via Spring Service!");
    }

    public List<Veiculo> listar() throws IOException {
        return dao.buscarTodos();
    }

}