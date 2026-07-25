package drl.desafio.servicio;

import drl.desafio.dominio.CuentaBancaria;

import java.util.List;
import java.util.Optional;

public interface RepositorioCuentas {
    void guardar(CuentaBancaria cuenta);
    Optional<CuentaBancaria> buscarPorNumero(String numeroCuenta);
    List<CuentaBancaria> buscarPorTitular(String identificacion);
    boolean existe(String numeroCuenta);
}
