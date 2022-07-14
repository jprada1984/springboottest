package net.jpradatech.springboottest;

import java.math.BigDecimal;
import java.util.Optional;
import net.jpradatech.springboottest.models.Banco;
import net.jpradatech.springboottest.models.Cuenta;

/** Datos class. */
public class Datos {

  public static final Optional<Cuenta> createCuenta001() {
    return Optional.of(new Cuenta(1L, "Jimmy", new BigDecimal("1000")));
  }

  public static final Optional<Cuenta> createCuenta002() {
    return Optional.of(new Cuenta(2L, "Lorena", new BigDecimal("2000")));
  }

  public static final Optional<Banco> createBanco() {
    return Optional.of(new Banco(1L, "Banco Central", 0));
  }
}
