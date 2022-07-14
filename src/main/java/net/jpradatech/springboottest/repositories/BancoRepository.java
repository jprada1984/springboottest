package net.jpradatech.springboottest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import net.jpradatech.springboottest.models.Banco;

public interface BancoRepository extends JpaRepository<Banco, Long> {

}
