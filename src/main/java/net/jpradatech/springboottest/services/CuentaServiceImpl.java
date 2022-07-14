package net.jpradatech.springboottest.services;

import java.math.BigDecimal;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.jpradatech.springboottest.models.Banco;
import net.jpradatech.springboottest.models.Cuenta;
import net.jpradatech.springboottest.repositories.BancoRepository;
import net.jpradatech.springboottest.repositories.CuentaRepository;

@Service
public class CuentaServiceImpl implements CuentaService {
	private CuentaRepository cuentaRepository;
	private BancoRepository bancoRepository;

	public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
		this.cuentaRepository = cuentaRepository;
		this.bancoRepository = bancoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Cuenta> findAll() {
		return cuentaRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Cuenta findById(Long id) {
		return cuentaRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Cuenta save(Cuenta cuenta) {
		return cuentaRepository.save(cuenta);
	}

	@Override
	@Transactional(readOnly = true)
	public int revisarTotalTransferencias(Long bancoId) {
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();
		return banco.getTotalTransferencias();
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal revisarSaldo(Long cuentaId) {
		Cuenta cuenta = cuentaRepository.findById(cuentaId).orElse(null);
		return cuenta.getSaldo();
	}

	@Override
	@Transactional
	public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId) {
		Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen).orElseThrow();
		cuentaOrigen.debito(monto);
		cuentaRepository.save(cuentaOrigen);

		Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino).orElseThrow();
		cuentaDestino.credito(monto);
		cuentaRepository.save(cuentaDestino);

		Banco banco = bancoRepository.findById(bancoId).orElseThrow();
		int totalTransferencias = banco.getTotalTransferencias();
		banco.setTotalTransferencias(++totalTransferencias);
		bancoRepository.save(banco);
	}
}
