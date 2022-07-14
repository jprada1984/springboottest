package net.jpradatech.springboottest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import net.jpradatech.springboottest.exceptions.DineroInsuficienteException;
import net.jpradatech.springboottest.models.Banco;
import net.jpradatech.springboottest.models.Cuenta;
import net.jpradatech.springboottest.repositories.BancoRepository;
import net.jpradatech.springboottest.repositories.CuentaRepository;
import net.jpradatech.springboottest.services.CuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SpringbootTestApplicationTests {

  @MockBean
  CuentaRepository cuentaRepository;
  @MockBean
  BancoRepository bancoRepository;
  @Autowired
  CuentaService service;

//  @BeforeEach
//  void setUp() {
//    cuentaRepository = mock(CuentaRepository.class);
//    bancoRepository = mock(BancoRepository.class);
//    service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
//  }

  @Test
  void contextLoads() {
    when(cuentaRepository.findById(1L)).thenReturn(Datos.createCuenta001());
    when(cuentaRepository.findById(2L)).thenReturn(Datos.createCuenta002());
    when(bancoRepository.findById(1L)).thenReturn(Datos.createBanco());

    BigDecimal saldoOrigen = service.revisarSaldo(1L);
    BigDecimal saldoDestino = service.revisarSaldo(2L);
    assertEquals("1000", saldoOrigen.toPlainString());
    assertEquals("2000", saldoDestino.toPlainString());

    service.transferir(1L, 2L, new BigDecimal("100"), 1L);

    saldoOrigen = service.revisarSaldo(1L);
    saldoDestino = service.revisarSaldo(2L);
    assertEquals("900", saldoOrigen.toPlainString());
    assertEquals("2100", saldoDestino.toPlainString());

    int total = service.revisarTotalTransferencias(1L);

    assertEquals(1, total);
    verify(cuentaRepository, times(3)).findById(1L);
    verify(cuentaRepository, times(3)).findById(2L);

    verify(cuentaRepository, times(2)).save(any(Cuenta.class));
    verify(bancoRepository, times(2)).findById(1L);
    verify(bancoRepository).save(any(Banco.class));

    verify(cuentaRepository, times(6)).findById(anyLong());
    verify(cuentaRepository, never()).findAll();
  }

  @Test
  void contextLoads2() {
    when(cuentaRepository.findById(1L)).thenReturn(Datos.createCuenta001());
    when(cuentaRepository.findById(2L)).thenReturn(Datos.createCuenta002());
    when(bancoRepository.findById(1L)).thenReturn(Datos.createBanco());

    BigDecimal saldoOrigen = service.revisarSaldo(1L);
    BigDecimal saldoDestino = service.revisarSaldo(2L);
    assertEquals("1000", saldoOrigen.toPlainString());
    assertEquals("2000", saldoDestino.toPlainString());

    assertThrows(DineroInsuficienteException.class,
        () -> service.transferir(1L, 2L, new BigDecimal("1200"), 1L));

    saldoOrigen = service.revisarSaldo(1L);
    saldoDestino = service.revisarSaldo(2L);

    assertEquals("1000", saldoOrigen.toPlainString());
    assertEquals("2000", saldoDestino.toPlainString());

    int total = service.revisarTotalTransferencias(1L);

    assertEquals(0, total);
    verify(cuentaRepository, times(3)).findById(1L);
    verify(cuentaRepository, times(2)).findById(2L);

    verify(cuentaRepository, never()).save(any(Cuenta.class));
    verify(bancoRepository).findById(1L);
    verify(bancoRepository, never()).save(any(Banco.class));
    verify(cuentaRepository, times(5)).findById(anyLong());
    verify(cuentaRepository, never()).findAll();
  }

  @Test
  void contextLoad3() {
    when(cuentaRepository.findById(1L)).thenReturn(Datos.createCuenta001());
    Cuenta cuenta1 = service.findById(1L);
    Cuenta cuenta2 = service.findById(1L);

    assertSame(cuenta1, cuenta2);
    assertTrue(cuenta1 == cuenta2);
    assertEquals("Jimmy", cuenta1.getPersona());
    assertEquals("Jimmy", cuenta2.getPersona());
    verify(cuentaRepository, times(2)).findById(1L);
  }


  @Test
  void testFindAll() {
    List<Cuenta> datos =
        Arrays.asList(Datos.createCuenta001().orElseThrow(), Datos.createCuenta002().orElseThrow());

		when(cuentaRepository.findAll()).thenReturn(datos);
		List<Cuenta> cuentas = service.findAll();

		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(Datos.createCuenta002().orElseThrow()));
  }

  @Test
  void testSave(){
    Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
    when(cuentaRepository.save(any())).then(invocation -> {
      Cuenta c = invocation.getArgument(0);
      c.setId(3L);
      return c;
    });

    Cuenta cuenta = service.save(cuentaPepe);

    assertEquals("Pepe", cuenta.getPersona());
    assertEquals(3L, cuenta.getId());
    assertEquals("3000", cuenta.getSaldo().toPlainString());

    verify(cuentaRepository).save(any());
  }
}
