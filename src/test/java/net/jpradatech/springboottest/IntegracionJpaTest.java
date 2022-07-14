package net.jpradatech.springboottest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import net.jpradatech.springboottest.models.Cuenta;
import net.jpradatech.springboottest.repositories.CuentaRepository;

@DataJpaTest
public class IntegracionJpaTest {

	@Autowired
	CuentaRepository cuentaRepository;

	@Test
	void testFindById() {
		Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
		assertTrue(cuenta.isPresent());
		assertEquals("Jimmy", cuenta.orElseThrow().getPersona());
	}

	@Test
	void testFindByPersona() {
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Jimmy");
		assertTrue(cuenta.isPresent());
		assertEquals("Jimmy", cuenta.orElseThrow().getPersona());
		assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
	}

	@Test
	void testFindByPersonaThrowException() {
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Luis");
		assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
		assertFalse(cuenta.isPresent());
	}

	@Test
	void testFindAll() {
		List<Cuenta> cuentas = cuentaRepository.findAll();
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
	}

	@Test
	void testSave() {
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		Cuenta cuenta = cuentaRepository.save(cuentaPepe);

		// Cuenta cuenta=cuentaRepository.findByPersona("Pepe").orElseThrow();
		// Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();

		assertEquals("Pepe", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
		// assertEquals(3L, cuenta.getId());
	}

	@Test
	void testUpdate() {
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		Cuenta cuenta = cuentaRepository.save(cuentaPepe);

		assertEquals("Pepe", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
		cuenta.setSaldo(new BigDecimal("3800"));

		Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
		assertEquals("Pepe", cuentaActualizada.getPersona());
		assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
	}

	@Test
	void testDelete() {
		Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
		assertEquals("Lorena", cuenta.getPersona());

		cuentaRepository.delete(cuenta);

		assertThrows(NoSuchElementException.class, () -> cuentaRepository.findByPersona("Lorena").orElseThrow());

		assertEquals(1, cuentaRepository.findAll().size());
	}
}
