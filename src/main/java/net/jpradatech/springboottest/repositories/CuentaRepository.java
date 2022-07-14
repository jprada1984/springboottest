package net.jpradatech.springboottest.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.jpradatech.springboottest.models.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

	@Query("select c from Cuenta c where c.persona=?1")
	Optional<Cuenta> findByPersona(String persona);
}
